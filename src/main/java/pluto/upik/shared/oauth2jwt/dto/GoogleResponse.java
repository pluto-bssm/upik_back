package pluto.upik.shared.oauth2jwt.dto;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class GoogleResponse implements OAuth2Response{

    private final Map<String, Object> attribute;

    @Override
    public String getProvider() {

        return "google";
    }

    @Override
    public String getProviderId() {

        return attribute.get("sub") != null ? attribute.get("sub").toString() : "";
    }

    @Override
    public String getEmail() {

        return attribute.get("email") != null ? attribute.get("email").toString() : "";
    }

    @Override
    public String getName() {

        return attribute.get("name") != null ? attribute.get("name").toString() : "";
    }
}