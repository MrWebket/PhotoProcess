/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shenghuoli.library.http;

import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a URI template. A URI template is a URI-like String that contains
 * variables enclosed by braces (<code>{</code>, <code>}</code>), which can be
 * expanded to produce an actual URI.
 * <p>
 * See {@link #expand(Map)}, {@link #expand(Object[])}, and
 * {@link #match(String)} for example usages.
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Roy Clarkson
 * @since 1.0
 * @see <a href="http://bitworking.org/projects/URI-Templates/">URI
 *      Templates</a>
 */
public class UriTemplate implements Serializable {

    /**
     * Static inner class to parse URI template strings into a matching regular
     * expression.
     */
    private static class Parser {

        private final StringBuilder patternBuilder = new StringBuilder();

        private final List<String> variableNames = new LinkedList<String>();

        private Parser(String uriTemplate) {
            Assert.hasText(uriTemplate, "'uriTemplate' must not be null");
            Matcher m = NAMES_PATTERN.matcher(uriTemplate);
            int end = 0;
            while (m.find()) {
                patternBuilder.append(quote(uriTemplate, end, m.start()));
                String match = m.group(1);
                int colonIdx = match.indexOf(':');
                if (colonIdx == -1) {
                    patternBuilder.append(DEFAULT_VARIABLE_PATTERN);
                    variableNames.add(match);
                } else {
                    if (colonIdx + 1 == match.length()) {
                        throw new IllegalArgumentException(
                                "No custom regular expression specified after ':' in \"" + match
                                        + "\"");
                    }
                    String variablePattern = match.substring(colonIdx + 1, match.length());
                    patternBuilder.append('(');
                    patternBuilder.append(variablePattern);
                    patternBuilder.append(')');
                    String variableName = match.substring(0, colonIdx);
                    variableNames.add(variableName);
                }
                end = m.end();
            }
            patternBuilder.append(quote(uriTemplate, end, uriTemplate.length()));
            int lastIdx = patternBuilder.length() - 1;
            if (lastIdx >= 0 && patternBuilder.charAt(lastIdx) == '/') {
                patternBuilder.deleteCharAt(lastIdx);
            }
        }

        private Pattern getMatchPattern() {
            return Pattern.compile(patternBuilder.toString());
        }

        private List<String> getVariableNames() {
            return Collections.unmodifiableList(variableNames);
        }

        private String quote(String fullPath, int start, int end) {
            if (start == end) {
                return "";
            }
            return Pattern.quote(fullPath.substring(start, end));
        }
    }

    /** Replaces template variables in the URI template. */
    private static final String DEFAULT_VARIABLE_PATTERN = "(.*)";

    /** Captures URI template variable names. */
    private static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");

    private static final long serialVersionUID = 1L;

    private final Pattern matchPattern;

    private final UriComponents uriComponents;

    private final String uriTemplate;

    private final List<String> variableNames;

    /**
     * Construct a new {@code UriTemplate} with the given URI String.
     * 
     * @param uriTemplate the URI template string
     */
    public UriTemplate(String uriTemplate) {
        Parser parser = new Parser(uriTemplate);
        this.uriTemplate = uriTemplate;
        variableNames = parser.getVariableNames();
        matchPattern = parser.getMatchPattern();
        uriComponents = UriComponentsBuilder.fromUriString(uriTemplate).build();
    }

    // expanding

    /**
     * Given the Map of variables, expands this template into a URI. The Map
     * keys represent variable names, the Map values variable values. The order
     * of variables is not significant.
     * <p>
     * Example:
     * 
     * <pre class="code">
     * UriTemplate template = new UriTemplate(&quot;http://example.com/hotels/{hotel}/bookings/{booking}&quot;);
     * Map&lt;String, String&gt; uriVariables = new HashMap&lt;String, String&gt;();
     * uriVariables.put(&quot;booking&quot;, &quot;42&quot;);
     * uriVariables.put(&quot;hotel&quot;, &quot;1&quot;);
     * System.out.println(template.expand(uriVariables));
     * </pre>
     * 
     * will print: <blockquote>
     * <code>http://example.com/hotels/1/bookings/42</code></blockquote>
     * 
     * @param uriVariables the map of URI variables
     * @return the expanded URI
     * @throws IllegalArgumentException if <code>uriVariables</code> is
     *             <code>null</code>; or if it does not contain values for all
     *             the variable names
     */
    public URI expand(Map<String, ?> uriVariables) {
        UriComponents expandedComponents = uriComponents.expand(uriVariables);
        UriComponents encodedComponents = expandedComponents.encode();
        return encodedComponents.toUri();
    }

    /**
     * Given an array of variables, expand this template into a full URI. The
     * array represent variable values. The order of variables is significant.
     * <p>
     * Example:
     * 
     * <pre class="code">
     * UriTemplate template = new UriTemplate("http://example.com/hotels/{hotel}/bookings/{booking}");
     * System.out.println(template.expand("1", "42));
     * </pre>
     * 
     * will print: <blockquote>
     * <code>http://example.com/hotels/1/bookings/42</code></blockquote>
     * 
     * @param uriVariableValues the array of URI variables
     * @return the expanded URI
     * @throws IllegalArgumentException if <code>uriVariables</code> is
     *             <code>null</code> or if it does not contain sufficient
     *             variables
     */
    public URI expand(Object... uriVariableValues) {
        UriComponents expandedComponents = uriComponents.expand(uriVariableValues);
        UriComponents encodedComponents = expandedComponents.encode();
        return encodedComponents.toUri();
    }

    // matching

    /**
     * Return the names of the variables in the template, in order.
     * 
     * @return the template variable names
     */
    public List<String> getVariableNames() {
        return variableNames;
    }

    /**
     * Match the given URI to a map of variable values. Keys in the returned map
     * are variable names, values are variable values, as occurred in the given
     * URI.
     * <p>
     * Example:
     * 
     * <pre class="code">
     * UriTemplate template = new UriTemplate(&quot;http://example.com/hotels/{hotel}/bookings/{booking}&quot;);
     * System.out.println(template.match(&quot;http://example.com/hotels/1/bookings/42&quot;));
     * </pre>
     * 
     * will print: <blockquote><code>{hotel=1, booking=42}</code></blockquote>
     * 
     * @param uri the URI to match to
     * @return a map of variable values
     */
    public Map<String, String> match(String uri) {
        Assert.notNull(uri, "'uri' must not be null");
        Map<String, String> result = new LinkedHashMap<String, String>(variableNames.size());
        Matcher matcher = matchPattern.matcher(uri);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String name = variableNames.get(i - 1);
                String value = matcher.group(i);
                result.put(name, value);
            }
        }
        return result;
    }

    /**
     * Indicate whether the given URI matches this template.
     * 
     * @param uri the URI to match to
     * @return <code>true</code> if it matches; <code>false</code> otherwise
     */
    public boolean matches(String uri) {
        if (uri == null) {
            return false;
        }
        Matcher matcher = matchPattern.matcher(uri);
        return matcher.matches();
    }

    @Override
    public String toString() {
        return uriTemplate;
    }

}
