package org.epam.xm.crypto.config;

import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.util.Collections;

import org.epam.xm.crypto.entity.CryptoEntity;
import org.epam.xm.crypto.model.CryptoInput;
import org.epam.xm.crypto.processor.CryptoInputProcessor;
import org.epam.xm.crypto.repository.CryptoRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * This class provides configurations for the batch process related to importing crypto-related data.
 * It sets up the necessary beans to read from a set of files, process the data and write it to a repository.
 */
@Configuration
public class BatchConfiguration {

    /** Path pattern to input files derived from application properties. */
    @Value("file:${app.files.sourceDir}${app.files.name-pattern}")
    private Resource[] inputFiles;

    /** Header columns for the file, as configured in application properties. */
    @Value("#{T(java.util.Arrays).asList('${app.files.header-columns}')}")
    private String[] fileHeaders;

    /** Chunk size for the batch processing, with a default value of 10000. */
    @Value("${app.files.chunks:10000}")
    private int chunks;

    /** Reference to the repository used for storing processed crypto data. */
    private final CryptoRepository cryptoRepository;

    /** Processor for transforming input data before writing to the repository. */
    private final CryptoInputProcessor cryptoInputProcessor;

    /**
     * Constructs a new batch configuration with provided repository and input processor.
     *
     * @param cryptoRepository      The repository for storing crypto data.
     * @param cryptoInputProcessor  The processor for crypto input data.
     */
    public BatchConfiguration(CryptoRepository cryptoRepository, CryptoInputProcessor cryptoInputProcessor) {
        this.cryptoRepository = cryptoRepository;
        this.cryptoInputProcessor = cryptoInputProcessor;
    }

    /**
     * Provides a bean to read data from a flat file.
     *
     * @return the item reader for crypto input.
     */
    @Bean
    public FlatFileItemReader<CryptoInput> cryptoInputReader() {
        // Initializing the builder for creating FlatFileItemReader for CryptoInput.
        FlatFileItemReaderBuilder<CryptoInput> builder = new FlatFileItemReaderBuilder<>();

        // Configuring the mapper to map fields from the file to the properties of CryptoInput.
        BeanWrapperFieldSetMapper<CryptoInput> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>() {
            {
                // Specifying the type of object that the fields will be mapped to.
                setTargetType(CryptoInput.class);

                // Adding custom editor for mapping Timestamp fields.
                // This allows the file to have long values which represent timestamps,
                // and they'll be automatically converted to Timestamp objects during field mapping.
                setCustomEditors(Collections.singletonMap(Timestamp.class, new PropertyEditorSupport() {
                    @Override
                    public void setAsText(String text) {
                        long timestampValue = Long.parseLong(text);
                        setValue(new Timestamp(timestampValue));
                    }
                }));
            }
        };

        return builder.name("cryptoInputReader")
                .linesToSkip(1)
                .delimited()
                .names(fileHeaders)
                .fieldSetMapper(beanWrapperFieldSetMapper)
                .build();
    }


    /**
     * Provides a bean to read data from multiple resources/files.
     *
     * @return the multi-resource item reader for crypto input.
     */
    @Bean
    @StepScope
    public MultiResourceItemReader<CryptoInput> multiCryptoResourceReader() {
        MultiResourceItemReader<CryptoInput> resourceItemReader = new MultiResourceItemReader<>();
        resourceItemReader.setResources(inputFiles);
        resourceItemReader.setDelegate(cryptoInputReader());
        return resourceItemReader;
    }

    /**
     * Provides a bean to write processed entities to the repository.
     *
     * @return the repository item writer for crypto entities.
     */
    @Bean
    public RepositoryItemWriter<CryptoEntity> cryptoEntityWriter() {
        return new RepositoryItemWriterBuilder<CryptoEntity>()
                .repository(cryptoRepository)
                .methodName("save")
                .build();
    }

    /**
     * Provides a job bean to handle the crypto CSV import task.
     *
     * @param jobRepository The job repository.
     * @param step          The step to be executed as part of the job.
     * @return the job for importing crypto CSV files.
     */
    @Bean(name = "cryptoCsvImportJob")
    public Job readCryptoCsvFiles(JobRepository jobRepository, Step step) {
        return new JobBuilder("cryptoCsvImportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    /**
     * Provides a step bean to handle chunks of data during the batch process.
     *
     * @param jobRepository        The job repository.
     * @param transactionManager   The platform transaction manager.
     * @param cryptoEntityWriter   The writer to handle the processed entities.
     * @return the step for processing chunks of crypto data.
     */
    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager, ItemWriter<CryptoEntity> cryptoEntityWriter) {
        return new StepBuilder("cryptoStepBuilder", jobRepository)
                .<CryptoInput, CryptoEntity>chunk(chunks, transactionManager)
                .reader(multiCryptoResourceReader())
                .processor(cryptoInputProcessor)
                .writer(cryptoEntityWriter)
                .build();
    }
}
