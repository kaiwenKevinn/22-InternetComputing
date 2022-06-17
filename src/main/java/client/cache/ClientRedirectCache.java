package client.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author Kevin
 * @Description
 * support 301 302 status code
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientRedirectCache {
    private HashMap<String, String> localStorage = new LinkedHashMap<>();

}
