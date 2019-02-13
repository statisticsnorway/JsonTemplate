package com.github.jsontemplate.valueproducer;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringNodeProducerTest {

    private StringNodeProducer producer = new StringNodeProducer();
    private int defaultLength = 5;

    @Test
    @DisplayName("generates a random string, the default length is 5")
    void testProduce() {
        String producedValue = producer.produce().print();
        assertThat(producedValue, allOf(startsWith("\""), endsWith("\"")));
        assertThat(producedValue.length(), is(defaultLength + 2));
    }

    @Test
    @DisplayName("generates a fixed string")
    void testProduceWithSingleParam() {
        String fixedValue = "myValue";
        String producedValue = producer.produce(fixedValue).print();
        assertThat(producedValue, is("\"" + fixedValue + "\""));
    }

    @Test
    @DisplayName("select a string from a list of enumerated string values")
    void testProduceWithListParam() {
        List<String> strings = Arrays.asList("A", "B", "C");
        String producedValue = producer.produce(strings).print();
        assertThat(producedValue, isIn(Arrays.asList("\"A\"", "\"B\"", "\"C\"")));
    }

    @Test
    @DisplayName("generate a string with a specified size")
    void testProduceWithParamSize() {
        Map<String, String> paramMap = new HashMap<>();

        int size = 11;
        paramMap.put("size", Integer.toString(size));

        String producedValue = producer.produce(paramMap).print();
        assertThat(producedValue, allOf(startsWith("\""), endsWith("\"")));
        assertThat(producedValue.length(), is(size + 2));
    }

    @Test
    @DisplayName("generate a string with the minimum size specified")
    void testProduceWithParamMin() {
        Map<String, String> paramMap = new HashMap<>();

        int min = 11;
        paramMap.put("min", Integer.toString(min));

        String producedValue = producer.produce(paramMap).print();
        assertThat(producedValue, allOf(startsWith("\""), endsWith("\"")));
        assertThat(producedValue.length(), greaterThanOrEqualTo(min + 2));
    }

    @Test
    @DisplayName("generate a string with the maximum size specified")
    void testProduceWithParamMax() {
        Map<String, String> paramMap = new HashMap<>();

        int max = 11;
        paramMap.put("max", Integer.toString(max));

        String producedValue = producer.produce(paramMap).print();
        assertThat(producedValue, allOf(startsWith("\""), endsWith("\"")));
        assertThat(producedValue.length(), lessThanOrEqualTo(max + 2));
    }

    @Test
    void testProduceWithParamMinMax() {
        Map<String, String> paramMap = new HashMap<>();

        int min = 6;
        int max = 11;
        paramMap.put("min", Integer.toString(min));
        paramMap.put("max", Integer.toString(max));

        String producedValue = producer.produce(paramMap).print();
        assertThat(producedValue, allOf(startsWith("\""), endsWith("\"")));
        assertThat(producedValue.length(), allOf(greaterThanOrEqualTo(min + 2), lessThanOrEqualTo(max + 2)));
    }

    @Test
    void testProduceWithParamSizeMinMax() {
        Map<String, String> paramMap = new HashMap<>();

        int size = 20;
        int min = 6;
        int max = 11;
        paramMap.put("size", Integer.toString(size));
        paramMap.put("min", Integer.toString(min));
        paramMap.put("max", Integer.toString(max));

        String producedValue = producer.produce(paramMap).print();
        assertThat(producedValue, allOf(startsWith("\""), endsWith("\"")));
        assertThat(producedValue.length(), is(size + 2));
    }

    @Test
    void testProduceWithUnsupportedParameter() {
        assertThrows(IllegalArgumentException.class, () -> {
            Map<String, String> paramMap = new HashMap<>();

            int length = 9;
            paramMap.put("length", Integer.toString(length));

            producer.produce(paramMap).print();
        });
    }
}