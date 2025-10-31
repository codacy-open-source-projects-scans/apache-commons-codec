/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.codec.language.bm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests PhoneticEngine.
 */
class PhoneticEngineTest {

    private static final Integer TEN = Integer.valueOf(10);

    private static final Pattern PIPE_PATTERN = Pattern.compile("\\|");
    private static final Pattern MINUS_PATTERN = Pattern.compile("-");

    public static Stream<Arguments> data() {
        // @formatter:off
        return Stream.of(
                Arguments.of("Renault", "rinD|rinDlt|rina|rinalt|rino|rinolt|rinu|rinult", NameType.GENERIC, RuleType.APPROX, Boolean.TRUE, TEN),
                Arguments.of("Renault", "rYnDlt|rYnalt|rYnult|rinDlt|rinalt|rinolt|rinult", NameType.ASHKENAZI, RuleType.APPROX, Boolean.TRUE, TEN),
                Arguments.of("Renault", "rinDlt", NameType.ASHKENAZI, RuleType.APPROX, Boolean.TRUE, Integer.valueOf(1)),
                Arguments.of("Renault", "rinDlt", NameType.SEPHARDIC, RuleType.APPROX, Boolean.TRUE, TEN),
                Arguments.of("SntJohn-Smith", "sntjonsmit", NameType.GENERIC, RuleType.EXACT, Boolean.TRUE, TEN),
                Arguments.of("d'ortley", "(ortlaj|ortlej)-(dortlaj|dortlej)", NameType.GENERIC, RuleType.EXACT, Boolean.TRUE, TEN),
                Arguments.of("van helsing", "(elSink|elsink|helSink|helsink|helzink|xelsink)-(banhelsink|fanhelsink|fanhelzink|vanhelsink|vanhelzink|vanjelsink)", NameType.GENERIC, RuleType.EXACT, Boolean.FALSE, TEN),
                Arguments.of("Judenburg", "iudnbYrk|iudnbirk|iudnburk|xudnbirk|xudnburk|zudnbirk|zudnburk", NameType.GENERIC, RuleType.APPROX, Boolean.TRUE, TEN),
                Arguments.of("Judenburg", "iudnbYrk|iudnbirk|iudnburk|xudnbirk|xudnburk|zudnbirk|zudnburk", NameType.GENERIC, RuleType.APPROX, Boolean.TRUE, Integer.MAX_VALUE)
                );
        // @formatter:on
    }

    public static Stream<Arguments> invalidData() {
        // @formatter:off
        return Stream.of(
                Arguments.of("bar", "bar|bor|var|vor", NameType.ASHKENAZI, RuleType.APPROX, Boolean.FALSE, TEN),
                Arguments.of("al", "|al", NameType.SEPHARDIC, RuleType.APPROX, Boolean.FALSE, TEN),
                Arguments.of("da", "da|di", NameType.GENERIC, RuleType.EXACT, Boolean.FALSE, TEN),
                Arguments.of("'''", "", NameType.SEPHARDIC, RuleType.APPROX, Boolean.FALSE, TEN),
                Arguments.of("'''", "", NameType.SEPHARDIC, RuleType.APPROX, Boolean.FALSE, Integer.MAX_VALUE)
                );
        // @formatter:on
    }

    // TODO Identify if there is a need to an assertTimeout(Duration.ofMillis(10000L) in some point, since this method was marked as @Test(timeout = 10000L)
    @ParameterizedTest
    @MethodSource("data")
    void testEncode(final String name, final String phoneticExpected, final NameType nameType,
                           final RuleType ruleType, final boolean concat, final int maxPhonemes) {
        final PhoneticEngine engine = new PhoneticEngine(nameType, ruleType, concat, maxPhonemes);

        final String phoneticActual = engine.encode(name);

        assertEquals(phoneticExpected, phoneticActual, "phoneme incorrect");

        if (concat) {
            final String[] split = PIPE_PATTERN.split(phoneticActual);
            assertTrue(split.length <= maxPhonemes);
        } else {
            final String[] words = MINUS_PATTERN.split(phoneticActual);
            for (final String word : words) {
                final String[] split = PIPE_PATTERN.split(word);
                assertTrue(split.length <= maxPhonemes);
            }
        }
    }

    @ParameterizedTest
    @MethodSource("invalidData")
    void testInvalidEncode(final String input, final String phoneticExpected, final NameType nameType,
                                  final RuleType ruleType, final boolean concat, final int maxPhonemes) {
        final PhoneticEngine engine = new PhoneticEngine(nameType, ruleType, concat, maxPhonemes);

        assertEquals(engine.encode(input), phoneticExpected);
    }
}
