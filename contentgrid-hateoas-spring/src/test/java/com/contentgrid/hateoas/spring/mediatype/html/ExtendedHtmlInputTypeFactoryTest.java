package com.contentgrid.hateoas.spring.mediatype.html;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.hateoas.mediatype.InputTypeFactory;

class ExtendedHtmlInputTypeFactoryTest {

    InputTypeFactory inputTypeFactory = new ExtendedHtmlInputTypeFactory();

    @ParameterizedTest
    @CsvSource({
            "java.lang.String,text",
            "java.lang.Short,number",
            "java.lang.Integer,number",
            "java.lang.Long,number",
            "java.lang.Float,number",
            "java.lang.Double,number",
            "java.math.BigDecimal,number",
            "java.lang.Boolean,checkbox",
            "java.time.LocalDate,date",
            "java.time.LocalDateTime,datetime-local",
            "java.time.LocalTime,time",
            "java.time.Instant,datetime",
            "java.util.UUID,text",
            "java.net.URI,url",
            "java.net.URL,url",
            "java.io.File,file"
    })
    void testInputType(Class<?> clazz, String inputType) {
        assertThat(inputTypeFactory.getInputType(clazz)).isEqualTo(inputType);
    }

}