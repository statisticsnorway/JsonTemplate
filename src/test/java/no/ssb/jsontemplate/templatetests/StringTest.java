package no.ssb.jsontemplate.templatetests;


import com.jayway.jsonpath.DocumentContext;
import no.ssb.jsontemplate.JsonTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static no.ssb.jsontemplate.templatetests.TestUtils.parse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


class StringTest {

    @Test
    @DisplayName("a random string field")
    void parseRandomString() {
        DocumentContext document = parse(new JsonTemplate("{aField : @s}"));
        assertThat(document.read("$.aField", String.class), is(notNullValue()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"myValue", "123", "1.2.3.4", "100%", "#123", "1*2/3-4"})
    void parseFixedString(String fixedValue) {
        String template = String.format("{aField : @s(%s)}", fixedValue);
        DocumentContext document = parse(new JsonTemplate(template));
        assertThat(document.read("$.aField", String.class), is(fixedValue));
    }

    @Test
    void test_nullString() {
        DocumentContext document = parse(new JsonTemplate("{aField : @s(null)}"));
        assertThat(document.read("$.aField", String.class), is(nullValue()));
    }

    @RepeatedTest(TestUtils.REPEATED_COUNT)
    void test_enumeratedStringField() {
        DocumentContext document = parse(new JsonTemplate("{aField : @s(A, B, C, D)}"));
        assertThat(document.read("$.aField", String.class), isIn(new String[]{"A", "B", "C", "D"}));
    }

    @RepeatedTest(TestUtils.REPEATED_COUNT)
    void test_enumeratedStringFieldWithLiteralNull() {
        DocumentContext document = parse(new JsonTemplate("{aField : @s(A, B, C, null)}"));
        assertThat(document.read("$.aField", String.class), isIn(new String[]{"A", "B", "C", "null"}));
    }

    @Test
    void test_sizedStringField() {
        DocumentContext document = parse(new JsonTemplate("{aField : @s(length=10)}"));
        assertThat(document.read("$.aField", String.class).length(), is(10));
    }

    @RepeatedTest(TestUtils.REPEATED_COUNT)
    void test_minParamStringField() {
        DocumentContext document = parse(new JsonTemplate("{aField : @s(min=11)}"));
        assertThat(document.read("$.aField", String.class).length(), greaterThanOrEqualTo(11));
    }


    @RepeatedTest(TestUtils.REPEATED_COUNT)
    void test_minMaxParamStringField() {
        DocumentContext document = parse(new JsonTemplate("{aField : @s(min=10, max=20)}"));
        assertThat(document.read("$.aField", String.class).length(), allOf(
                greaterThanOrEqualTo(10), lessThanOrEqualTo(20)));
    }

    @Test
    void test_rawString() {
        String rawContent = "!@#$%^&*()-= \t\n{}[]abc";
        DocumentContext document = parse(new JsonTemplate("{aField : @s(`" + rawContent + "`)}"));
        assertThat(document.read("$.aField", String.class), is(rawContent));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{aField : @s(length=-10)}", // minusSizedStringField
            "{aField : @s(min=-1)}", // minusMinParamStringField
            "{aField : @s(min=20, max=10)}", // invalidRangeParamStringField
            "{aField : @s(size=20)}", // invalidParamStringField
    })
    void test_invalidParam(String templateString) {
        JsonTemplate jsonTemplate = new JsonTemplate(templateString);
        assertThrows(IllegalArgumentException.class, () -> parse(jsonTemplate)
        );
    }
}


