package org.example.enums;

import org.example.error.HttpRequestParserException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Enum representing URL path parameters and query parameters used in requests. These parameters do not include the root
 * directory but encompass key-value pairs found in the URLs.
 */
public enum UrlParameters {

    NAME("name"),
    ID("id"),
    QUERY("query"),
    ACTION("action"),
    VALUE("value"),
    OVERRIDE("override"),
    UPDATE_NAME("update-name");

    private final String parameter;

    UrlParameters(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }

    /**
     * Retrieves the corresponding UrlPathParameters enum based on the given string input. If the input does not match
     * any known parameter, an HttpRequestReaderException is thrown.
     *
     * @param input the parameter key from the URL (e.g., "action", "name", "id").
     * @return the corresponding UrlPathParameters enum.
     * @throws HttpRequestParserException if the input does not match any known parameter.
     */
    public static UrlParameters getParameter(String input) throws HttpRequestParserException {
        for (UrlParameters parameter : UrlParameters.values()) {
            if (parameter.getParameter().equals(input)) {
                return parameter;
            }
        }
        throw new HttpRequestParserException("URL malformed; '" + input + "' is not part of a valid URL structure",
                HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
    }

    /**
     * Parses a query string and returns a map of URL parameters to their corresponding values. The query string should
     * be formatted as key-value pairs, separated by '=' and '&'.
     *
     * Example:
     * Input: "action=update-name&update=newName"
     * Output: {ACTION=update-name, UPDATE_NAME=newName}
     *
     * @param query the raw query string from the URL.
     * @return a LinkedHashMap mapping UrlPathParameters to their respective values.
     * @throws HttpRequestParserException if an unknown parameter is encountered.
     */
    public static Map<UrlParameters, String> mapQueryValues(String query) throws HttpRequestParserException {
        Map<UrlParameters, String> map = new LinkedHashMap<>();
        String[] splitQuery = query.split("[=&]");
        for (int i = 0; i < splitQuery.length; i += 2) {
            map.put(UrlParameters.getParameter(splitQuery[i]), splitQuery[i + 1]);
        }
        return map;
    }
}
