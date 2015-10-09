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

import com.shenghuoli.library.utils.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Represents an immutable collection of URI components, mapping component type to string values. Contains convenience
 * getters for all components. Effectively similar to {@link URI}, but with more powerful encoding options and support
 * for URI template variables.
 *
 * @author Arjen Poutsma
 * @since 1.0
 * @see UriComponentsBuilder
 */
final class UriComponents {

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final char PATH_DELIMITER = '/';

	/** Captures URI template variable names. */
	private static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");

	private final String scheme;

	private final String userInfo;

	private final String host;

	private final int port;

	private final PathComponent path;

	private final MultiValueMap<String, String> queryParams;

	private final String fragment;

    private final boolean encoded;

	/**
	 * Package-friendly constructor that creates a new {@code UriComponents} instance from the given parameters. All
	 * parameters are optional, and can be {@code null}.
	 *
	 * @param scheme the scheme
	 * @param userInfo the user info
	 * @param host the host
	 * @param port the port
	 * @param path the path component
	 * @param queryParams the query parameters
	 * @param fragment the fragment
	 * @param encoded whether the components are encoded
	 * @param verify whether the components need to be verified to determine whether they contain illegal characters
	 */
	UriComponents(String scheme,
				  String userInfo,
				  String host,
				  int port,
				  PathComponent path,
				  MultiValueMap<String, String> queryParams,
				  String fragment,
				  boolean encoded,
				  boolean verify) {
		this.scheme = scheme;
		this.userInfo = userInfo;
		this.host = host;
		this.port = port;
		this.path = path != null ? path : NULL_PATH_COMPONENT;
		this.queryParams = unmodifiableMultiValueMap(queryParams != null ? queryParams : new LinkedMultiValueMap<String, String>(0));
		this.fragment = fragment;
		this.encoded = encoded;
		if (verify) {
			verify();
		}
	}
	
	/**
	 * Adapts a {@code Map<K, List<V>>} to an {@code MultiValueMap<K,V>}.
	 * 
	 * @param map the map
	 * @return the multi-value map
	 */
	public static <K, V> MultiValueMap<K, V> toMultiValueMap(Map<K, List<V>> map) {
		return new MultiValueMapAdapter<K, V>(map);

	}

	/**
	 * Returns an unmodifiable view of the specified multi-value map.
	 * 
	 * @param map the map for which an unmodifiable view is to be returned.
	 * @return an unmodifiable view of the specified multi-value map.
	 */
	public static <K, V> MultiValueMap<K, V> unmodifiableMultiValueMap(MultiValueMap<? extends K, ? extends V> map) {
		Assert.notNull(map, "'map' must not be null");
		Map<K, List<V>> result = new LinkedHashMap<K, List<V>>(map.size());
		for (Map.Entry<? extends K, ? extends List<? extends V>> entry : map.entrySet()) {
			List<V> values = Collections.unmodifiableList(entry.getValue());
			result.put(entry.getKey(), values);
		}
		Map<K, List<V>> unmodifiableMap = Collections.unmodifiableMap(result);
		return toMultiValueMap(unmodifiableMap);
	}

	// component getters

    /**
     * Returns the scheme.
     *
     * @return the scheme. Can be {@code null}.
     */
    public String getScheme() {
		return scheme;
    }

    /**
     * Returns the user info.
     *
     * @return the user info. Can be {@code null}.
     */
    public String getUserInfo() {
		return userInfo;
    }

    /**
     * Returns the host.
     *
     * @return the host. Can be {@code null}.
     */
    public String getHost() {
		return host;
    }

    /**
     * Returns the port. Returns {@code -1} if no port has been set.
     *
     * @return the port
     */
    public int getPort() {
		return port;
    }

	/**
	 * Returns the path.
	 *
	 * @return the path. Can be {@code null}.
	 */
	public String getPath() {
		return path.getPath();
	}

    /**
     * Returns the list of path segments.
     *
     * @return the path segments. Empty if no path has been set.
     */
	public List<String> getPathSegments() {
		return path.getPathSegments();
	}

	/**
	 * Returns the query.
	 *
	 * @return the query. Can be {@code null}.
	 */
	public String getQuery() {
		if (!queryParams.isEmpty()) {
			StringBuilder queryBuilder = new StringBuilder();
			for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
				String name = entry.getKey();
				List<String> values = entry.getValue();
				if (isEmpty(values)) {
					if (queryBuilder.length() != 0) {
						queryBuilder.append('&');
					}
					queryBuilder.append(name);
				}
				else {
					for (Object value : values) {
						if (queryBuilder.length() != 0) {
							queryBuilder.append('&');
						}
						queryBuilder.append(name);

						if (value != null) {
							queryBuilder.append('=');
							queryBuilder.append(value.toString());
						}
					}
				}
			}
			return queryBuilder.toString();
		}
		else {
			return null;
		}
	}
	
	/**
	 * Return <code>true</code> if the supplied Collection is <code>null</code> or empty. Otherwise, return
	 * <code>false</code>.
	 * @param collection the Collection to check
	 * @return whether the given Collection is empty
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return (collection == null || collection.isEmpty());
	}

	/**
     * Returns the map of query parameters.
     *
     * @return the query parameters. Empty if no query has been set.
     */
    public MultiValueMap<String, String> getQueryParams() {
		return queryParams;
    }

    /**
     * Returns the fragment.
     *
     * @return the fragment. Can be {@code null}.
     */
    public String getFragment() {
		return fragment;
    }

    // encoding

	/**
	 * Encodes all URI components using their specific encoding rules, and returns the result as a new
	 * {@code UriComponents} instance. This method uses UTF-8 to encode.
	 *
	 * @return the encoded uri components
	 */
    public UriComponents encode() {
        try {
            return encode(DEFAULT_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            throw new InternalError("\"" + DEFAULT_ENCODING + "\" not supported");
        }
    }

    /**
     * Encodes all URI components using their specific encoding rules, and returns the result as a new
     * {@code UriComponents} instance.
     *
     * @param encoding the encoding of the values contained in this map
     * @return the encoded uri components
     * @throws UnsupportedEncodingException if the given encoding is not supported
     */
    public UriComponents encode(String encoding) throws UnsupportedEncodingException {
        Assert.hasLength(encoding, "'encoding' must not be empty");

        if (encoded) {
            return this;
        }

		String encodedScheme = encodeUriComponent(this.scheme, encoding, Type.SCHEME);
		String encodedUserInfo = encodeUriComponent(this.userInfo, encoding, Type.USER_INFO);
		String encodedHost = encodeUriComponent(this.host, encoding, Type.HOST);
		PathComponent encodedPath = path.encode(encoding);
		MultiValueMap<String, String> encodedQueryParams =
				new LinkedMultiValueMap<String, String>(this.queryParams.size());
		for (Map.Entry<String, List<String>> entry : this.queryParams.entrySet()) {
			String encodedName = encodeUriComponent(entry.getKey(), encoding, Type.QUERY_PARAM);
			List<String> encodedValues = new ArrayList<String>(entry.getValue().size());
			for (String value : entry.getValue()) {
				String encodedValue = encodeUriComponent(value, encoding, Type.QUERY_PARAM);
				encodedValues.add(encodedValue);
			}
			encodedQueryParams.put(encodedName, encodedValues);
		}
		String encodedFragment = encodeUriComponent(this.fragment, encoding, Type.FRAGMENT);

		return new UriComponents(encodedScheme, encodedUserInfo, encodedHost, this.port, encodedPath,
				encodedQueryParams, encodedFragment, true, false);
    }

    /**
     * Encodes the given source into an encoded String using the rules specified by the given component and with the
     * given options.
     *
     * @param source the source string
     * @param encoding the encoding of the source string
     * @param type the URI component for the source
     * @return the encoded URI
     * @throws IllegalArgumentException when the given uri parameter is not a valid URI
     */
	static String encodeUriComponent(String source, String encoding, Type type)
			throws UnsupportedEncodingException {
		if (source == null) {
			return null;
		}

		Assert.hasLength(encoding, "'encoding' must not be empty");

		byte[] bytes = encodeBytes(source.getBytes(encoding), type);
		return new String(bytes, "US-ASCII");
	}

	private static byte[] encodeBytes(byte[] source, Type type) {
		Assert.notNull(source, "'source' must not be null");
        Assert.notNull(type, "'type' must not be null");

        ByteArrayOutputStream bos = new ByteArrayOutputStream(source.length);
        for (int i = 0; i < source.length; i++) {
            int b = source[i];
            if (b < 0) {
                b += 256;
            }
            if (type.isAllowed(b)) {
                bos.write(b);
            }
            else {
                bos.write('%');

                char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
                char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));

                bos.write(hex1);
                bos.write(hex2);
            }
        }
        return bos.toByteArray();
    }

	// verifying

	/**
	 * Verifies all URI components to determine whether they contain any illegal characters, throwing an
	 * {@code IllegalArgumentException} if so.
	 *
	 * @throws IllegalArgumentException if any of the components contain illegal characters
	 */
	private void verify() {
		if (!encoded) {
			return;
		}
		verifyUriComponent(scheme, Type.SCHEME);
		verifyUriComponent(userInfo, Type.USER_INFO);
		verifyUriComponent(host, Type.HOST);
		path.verify();
		for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
			verifyUriComponent(entry.getKey(), Type.QUERY_PARAM);
			for (String value : entry.getValue()) {
				verifyUriComponent(value, Type.QUERY_PARAM);
			}
		}
		verifyUriComponent(fragment, Type.FRAGMENT);
	}


	private static void verifyUriComponent(String source, Type type) {
		if (source == null) {
			return;
		}

		int length = source.length();

		for (int i=0; i < length; i++) {
			char ch = source.charAt(i);
			if (ch == '%') {
				if ((i + 2) < length) {
					char hex1 = source.charAt(i + 1);
					char hex2 = source.charAt(i + 2);
					int u = Character.digit(hex1, 16);
					int l = Character.digit(hex2, 16);
					if (u == -1 || l == -1) {
						throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
					}
					i += 2;
				}
				else {
					throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
				}
			}
			else if (!type.isAllowed(ch)) {
				throw new IllegalArgumentException(
						"Invalid character '" + ch + "' for " + type.name() + " in \"" + source + "\"");
			}
		}
	}

	// expanding

	/**
	 * Replaces all URI template variables with the values from a given map. The map keys represent
	 * variable names; the values variable values. The order of variables is not significant.

	 * @param uriVariables the map of URI variables
	 * @return the expanded uri components
	 */
	public UriComponents expand(Map<String, ?> uriVariables) {
		Assert.notNull(uriVariables, "'uriVariables' must not be null");

		return expandInternal(new MapTemplateVariables(uriVariables));
	}

	/**
	 * Replaces all URI template variables with the values from a given array. The array represent variable values.
	 * The order of variables is significant.

	 * @param uriVariableValues URI variable values
	 * @return the expanded uri components
	 */
	public UriComponents expand(Object... uriVariableValues) {
		Assert.notNull(uriVariableValues, "'uriVariableValues' must not be null");

		return expandInternal(new VarArgsTemplateVariables(uriVariableValues));
	}

	private UriComponents expandInternal(UriTemplateVariables uriVariables) {
		Assert.state(!encoded, "Cannot expand an already encoded UriComponents object");

		String expandedScheme = expandUriComponent(this.scheme, uriVariables);
		String expandedUserInfo = expandUriComponent(this.userInfo, uriVariables);
		String expandedHost = expandUriComponent(this.host, uriVariables);
		PathComponent expandedPath = path.expand(uriVariables);
		MultiValueMap<String, String> expandedQueryParams =
				new LinkedMultiValueMap<String, String>(this.queryParams.size());
		for (Map.Entry<String, List<String>> entry : this.queryParams.entrySet()) {
			String expandedName = expandUriComponent(entry.getKey(), uriVariables);
			List<String> expandedValues = new ArrayList<String>(entry.getValue().size());
			for (String value : entry.getValue()) {
				String expandedValue = expandUriComponent(value, uriVariables);
				expandedValues.add(expandedValue);
			}
			expandedQueryParams.put(expandedName, expandedValues);
		}
		String expandedFragment = expandUriComponent(this.fragment, uriVariables);

		return new UriComponents(expandedScheme, expandedUserInfo, expandedHost, this.port, expandedPath,
				expandedQueryParams, expandedFragment, false, false);
	}

	private static String expandUriComponent(String source, UriTemplateVariables uriVariables) {
		if (source == null) {
			return null;
		}
		if (source.indexOf('{') == -1) {
			return source;
		}
		Matcher matcher = NAMES_PATTERN.matcher(source);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String match = matcher.group(1);
			String variableName = getVariableName(match);
			Object variableValue = uriVariables.getValue(variableName);
			String variableValueString = getVariableValueAsString(variableValue);
			String replacement = Matcher.quoteReplacement(variableValueString);
			matcher.appendReplacement(sb, replacement);
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	private static String getVariableName(String match) {
		int colonIdx = match.indexOf(':');
		return colonIdx == -1 ? match : match.substring(0, colonIdx);
	}

	private static String getVariableValueAsString(Object variableValue) {
		return variableValue != null ? variableValue.toString() : "";
	}

    /**
     * Returns a URI string from this {@code UriComponents} instance.
     *
     * @return the URI string
     */
    public String toUriString() {
        StringBuilder uriBuilder = new StringBuilder();

        if (scheme != null) {
            uriBuilder.append(scheme);
            uriBuilder.append(':');
        }

        if (userInfo != null || host != null) {
            uriBuilder.append("//");
            if (userInfo != null) {
				uriBuilder.append(userInfo);
                uriBuilder.append('@');
            }
            if (host != null) {
				uriBuilder.append(host);
            }
            if (port != -1) {
                uriBuilder.append(':');
				uriBuilder.append(port);
            }
        }

		String path = getPath();
		if (StringUtils.hasLength(path)) {
			if (uriBuilder.length() != 0 && path.charAt(0) != PATH_DELIMITER) {
				uriBuilder.append(PATH_DELIMITER);
			}
			uriBuilder.append(path);
		}

		String query = getQuery();
		if (query != null) {
            uriBuilder.append('?');
            uriBuilder.append(query);
        }

        if (fragment != null) {
            uriBuilder.append('#');
			uriBuilder.append(fragment);
        }

        return uriBuilder.toString();
    }

    /**
     * Returns a {@code URI} from this {@code UriComponents} instance.
     *
     * @return the URI
     */
    public URI toUri() {
        try {
            if (encoded) {
                return new URI(toUriString());
            }
            else {
				String path = getPath();
				if (StringUtils.hasLength(path) && path.charAt(0) != PATH_DELIMITER) {
					path = PATH_DELIMITER + path;
				}
				return new URI(getScheme(), getUserInfo(), getHost(), getPort(), path, getQuery(),
						getFragment());
            }
        }
        catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not create URI object: " + ex.getMessage(), ex);
        }
    }

	@Override
    public String toString() {
		return toUriString();
    }

    // inner types

    /**
     * Enumeration used to identify the parts of a URI.
     * <p/>
     * Contains methods to indicate whether a given character is valid in a specific URI component.
     *
     * @author Arjen Poutsma
     * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986</a>
     */
    static enum Type {

        SCHEME {
            @Override
            public boolean isAllowed(int c) {
                return isAlpha(c) || isDigit(c) || '+' == c || '-' == c || '.' == c;
            }
        },
        AUTHORITY {
            @Override
            public boolean isAllowed(int c) {
                return isUnreserved(c) || isSubDelimiter(c) || ':' == c || '@' == c;
            }
        },
        USER_INFO {
            @Override
            public boolean isAllowed(int c) {
                return isUnreserved(c) || isSubDelimiter(c) || ':' == c;
            }
        },
        HOST {
            @Override
            public boolean isAllowed(int c) {
                return isUnreserved(c) || isSubDelimiter(c);
            }
        },
        PORT {
            @Override
            public boolean isAllowed(int c) {
                return isDigit(c);
            }
        },
        PATH {
            @Override
            public boolean isAllowed(int c) {
                return isPchar(c) || '/' == c;
            }
        },
        PATH_SEGMENT {
            @Override
            public boolean isAllowed(int c) {
                return isPchar(c);
            }
        },
        QUERY {
            @Override
            public boolean isAllowed(int c) {
                return isPchar(c) || '/' == c || '?' == c;
            }
        },
        QUERY_PARAM {
            @Override
            public boolean isAllowed(int c) {
                if ('=' == c || '+' == c || '&' == c) {
                    return false;
                }
                else {
                    return isPchar(c) || '/' == c || '?' == c;
                }
            }
        },
        FRAGMENT {
            @Override
            public boolean isAllowed(int c) {
                return isPchar(c) || '/' == c || '?' == c;
            }
        };

        /**
         * Indicates whether the given character is allowed in this URI component.
         *
         * @param c the character
         * @return {@code true} if the character is allowed; {@code false} otherwise
         */
        public abstract boolean isAllowed(int c);

        /**
         * Indicates whether the given character is in the {@code ALPHA} set.
         *
         * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
         */
        protected boolean isAlpha(int c) {
            return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
        }

        /**
         * Indicates whether the given character is in the {@code DIGIT} set.
         *
         * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
         */
        protected boolean isDigit(int c) {
            return c >= '0' && c <= '9';
        }

        /**
         * Indicates whether the given character is in the {@code gen-delims} set.
         *
         * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
         */
        protected boolean isGenericDelimiter(int c) {
            return ':' == c || '/' == c || '?' == c || '#' == c || '[' == c || ']' == c || '@' == c;
        }

        /**
         * Indicates whether the given character is in the {@code sub-delims} set.
         *
         * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
         */
        protected boolean isSubDelimiter(int c) {
            return '!' == c || '$' == c || '&' == c || '\'' == c || '(' == c || ')' == c || '*' == c || '+' == c ||
                    ',' == c || ';' == c || '=' == c;
        }

        /**
         * Indicates whether the given character is in the {@code reserved} set.
         *
         * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
         */
        protected boolean isReserved(char c) {
            return isGenericDelimiter(c) || isReserved(c);
        }

        /**
         * Indicates whether the given character is in the {@code unreserved} set.
         *
         * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
         */
        protected boolean isUnreserved(int c) {
            return isAlpha(c) || isDigit(c) || '-' == c || '.' == c || '_' == c || '~' == c;
        }

        /**
         * Indicates whether the given character is in the {@code pchar} set.
         *
         * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
         */
        protected boolean isPchar(int c) {
            return isUnreserved(c) || isSubDelimiter(c) || ':' == c || '@' == c;
        }

    }

	/**
	 * Defines the contract for path (segments).
	 */
	interface PathComponent {

		String getPath();

		List<String> getPathSegments();

		PathComponent encode(String encoding) throws UnsupportedEncodingException;

		void verify();

		PathComponent expand(UriTemplateVariables uriVariables);

	}

	/**
	 * Represents a path backed by a string.
	 */
	final static class FullPathComponent implements PathComponent {

		private final String path;

		FullPathComponent(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}

		public List<String> getPathSegments() {
			String delimiter = new String(new char[]{PATH_DELIMITER});
			String[] pathSegments = StringUtils.tokenizeToStringArray(path, delimiter);
			return Collections.unmodifiableList(Arrays.asList(pathSegments));
		}

		public PathComponent encode(String encoding) throws UnsupportedEncodingException {
			String encodedPath = encodeUriComponent(getPath(),encoding, Type.PATH);
			return new FullPathComponent(encodedPath);
		}

		public void verify() {
			verifyUriComponent(path, Type.PATH);
		}

		public PathComponent expand(UriTemplateVariables uriVariables) {
			String expandedPath = expandUriComponent(getPath(), uriVariables);
			return new FullPathComponent(expandedPath);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o instanceof FullPathComponent) {
				FullPathComponent other = (FullPathComponent) o;
				return this.getPath().equals(other.getPath());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return getPath().hashCode();
		}
	}

	/**
	 * Represents a path backed by a string list (i.e. path segments).
	 */
	final static class PathSegmentComponent implements PathComponent {

		private final List<String> pathSegments;

		PathSegmentComponent(List<String> pathSegments) {
			this.pathSegments = Collections.unmodifiableList(pathSegments);
		}

		public String getPath() {
			StringBuilder pathBuilder = new StringBuilder();
			pathBuilder.append(PATH_DELIMITER);
			for (Iterator<String> iterator = pathSegments.iterator(); iterator.hasNext(); ) {
				String pathSegment = iterator.next();
				pathBuilder.append(pathSegment);
				if (iterator.hasNext()) {
					pathBuilder.append(PATH_DELIMITER);
				}
			}
			return pathBuilder.toString();
		}

		public List<String> getPathSegments() {
			return pathSegments;
		}

		public PathComponent encode(String encoding) throws UnsupportedEncodingException {
			List<String> pathSegments = getPathSegments();
			List<String> encodedPathSegments = new ArrayList<String>(pathSegments.size());
			for (String pathSegment : pathSegments) {
				String encodedPathSegment = encodeUriComponent(pathSegment, encoding, Type.PATH_SEGMENT);
				encodedPathSegments.add(encodedPathSegment);
			}
			return new PathSegmentComponent(encodedPathSegments);
		}

		public void verify() {
			for (String pathSegment : getPathSegments()) {
				verifyUriComponent(pathSegment, Type.PATH_SEGMENT);
			}
		}

		public PathComponent expand(UriTemplateVariables uriVariables) {
			List<String> pathSegments = getPathSegments();
			List<String> expandedPathSegments = new ArrayList<String>(pathSegments.size());
			for (String pathSegment : pathSegments) {
				String expandedPathSegment = expandUriComponent(pathSegment, uriVariables);
				expandedPathSegments.add(expandedPathSegment);
			}
			return new PathSegmentComponent(expandedPathSegments);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o instanceof PathSegmentComponent) {
				PathSegmentComponent other = (PathSegmentComponent) o;
				return this.getPathSegments().equals(other.getPathSegments());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return getPathSegments().hashCode();
		}

	}

	/**
	 * Represents a collection of PathComponents.
	 */
	final static class PathComponentComposite implements PathComponent {

		private final List<PathComponent> pathComponents;

		PathComponentComposite(List<PathComponent> pathComponents) {
			this.pathComponents = pathComponents;
		}

		public String getPath() {
			StringBuilder pathBuilder = new StringBuilder();
			for (PathComponent pathComponent : pathComponents) {
				pathBuilder.append(pathComponent.getPath());
			}
			return pathBuilder.toString();
		}

		public List<String> getPathSegments() {
			List<String> result = new ArrayList<String>();
			for (PathComponent pathComponent : pathComponents) {
				result.addAll(pathComponent.getPathSegments());
			}
			return result;
		}

		public PathComponent encode(String encoding) throws UnsupportedEncodingException {
			List<PathComponent> encodedComponents = new ArrayList<PathComponent>(pathComponents.size());
			for (PathComponent pathComponent : pathComponents) {
				encodedComponents.add(pathComponent.encode(encoding));
			}
			return new PathComponentComposite(encodedComponents);
		}

		public void verify() {
			for (PathComponent pathComponent : pathComponents) {
				pathComponent.verify();
			}
		}

		public PathComponent expand(UriTemplateVariables uriVariables) {
			List<PathComponent> expandedComponents = new ArrayList<PathComponent>(pathComponents.size());
			for (PathComponent pathComponent : pathComponents) {
				expandedComponents.add(pathComponent.expand(uriVariables));
			}
			return new PathComponentComposite(expandedComponents);
		}
	}



	/**
	 * Represents an empty path.
	 */
	final static PathComponent NULL_PATH_COMPONENT = new PathComponent() {

		public String getPath() {
			return null;
		}

		public List<String> getPathSegments() {
			return Collections.emptyList();
		}

		public PathComponent encode(String encoding) throws UnsupportedEncodingException {
			return this;
		}

		public void verify() {
		}

		public PathComponent expand(UriTemplateVariables uriVariables) {
			return this;
		}

		@Override
		public boolean equals(Object o) {
			return this == o;
		}

		@Override
		public int hashCode() {
			return 42;
		}

	};

	/**
	 * Defines the contract for URI Template variables
	 *
	 * @see UriComponents#expand
	 */
	private interface UriTemplateVariables {

		Object getValue(String name);


	}

	/**
	 * URI template variables backed by a map.
	 */
	private static class MapTemplateVariables implements UriTemplateVariables {

		private final Map<String, ?> uriVariables;

		public MapTemplateVariables(Map<String, ?> uriVariables) {
			this.uriVariables = uriVariables;
		}

		public Object getValue(String name) {
			if (!this.uriVariables.containsKey(name)) {
				throw new IllegalArgumentException("Map has no value for '" + name + "'");
			}
			return this.uriVariables.get(name);
		}
	}

	/**
	 * URI template variables backed by a variable argument array.
	 */
	private static class VarArgsTemplateVariables implements UriTemplateVariables {

		private final Iterator<Object> valueIterator;

		public VarArgsTemplateVariables(Object... uriVariableValues) {
			this.valueIterator = Arrays.asList(uriVariableValues).iterator();
		}

		public Object getValue(String name) {
			if (!valueIterator.hasNext()) {
				throw new IllegalArgumentException("Not enough variable values available to expand '" + name + "'");
			}
			return valueIterator.next();
		}
	}

	/**
	 * Adapts a Map to the MultiValueMap contract.
	 */
	private static class MultiValueMapAdapter<K, V> implements MultiValueMap<K, V>, Serializable {

		private static final long serialVersionUID = 1L;

		private final Map<K, List<V>> map;

		public MultiValueMapAdapter(Map<K, List<V>> map) {
			Assert.notNull(map, "'map' must not be null");
			this.map = map;
		}

		public void add(K key, V value) {
			List<V> values = this.map.get(key);
			if (values == null) {
				values = new LinkedList<V>();
				this.map.put(key, values);
			}
			values.add(value);
		}

		public V getFirst(K key) {
			List<V> values = this.map.get(key);
			return (values != null ? values.get(0) : null);
		}

		public void set(K key, V value) {
			List<V> values = new LinkedList<V>();
			values.add(value);
			this.map.put(key, values);
		}

		public void setAll(Map<K, V> values) {
			for (Entry<K, V> entry : values.entrySet()) {
				set(entry.getKey(), entry.getValue());
			}
		}

		public Map<K, V> toSingleValueMap() {
			LinkedHashMap<K, V> singleValueMap = new LinkedHashMap<K, V>(this.map.size());
			for (Entry<K, List<V>> entry : map.entrySet()) {
				singleValueMap.put(entry.getKey(), entry.getValue().get(0));
			}
			return singleValueMap;
		}

		public int size() {
			return this.map.size();
		}

		public boolean isEmpty() {
			return this.map.isEmpty();
		}

		public boolean containsKey(Object key) {
			return this.map.containsKey(key);
		}

		public boolean containsValue(Object value) {
			return this.map.containsValue(value);
		}

		public List<V> get(Object key) {
			return this.map.get(key);
		}

		public List<V> put(K key, List<V> value) {
			return this.map.put(key, value);
		}

		public List<V> remove(Object key) {
			return this.map.remove(key);
		}

		public void putAll(Map<? extends K, ? extends List<V>> m) {
			this.map.putAll(m);
		}

		public void clear() {
			this.map.clear();
		}

		public Set<K> keySet() {
			return this.map.keySet();
		}

		public Collection<List<V>> values() {
			return this.map.values();
		}

		public Set<Entry<K, List<V>>> entrySet() {
			return this.map.entrySet();
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			return map.equals(other);
		}

		@Override
		public int hashCode() {
			return this.map.hashCode();
		}

		@Override
		public String toString() {
			return this.map.toString();
		}
	}
}