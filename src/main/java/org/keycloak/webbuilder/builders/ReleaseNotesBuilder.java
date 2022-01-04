package org.keycloak.webbuilder.builders;

import org.keycloak.webbuilder.Versions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ReleaseNotesBuilder extends AbstractBuilder {

    private static final String BASE_URL = "https://raw.githubusercontent.com/keycloak/keycloak-documentation/master/";
    private static final String DOCUMENT_ATTRIBUTES_URL = BASE_URL + "topics/templates/document-attributes-community.adoc";

    @Override
    protected String getTitle() {
        return "Release Notes";
    }

    public void build() throws IOException {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("project_buildType", "latest");
        attributes.put("leveloffset", "2");
        attributes.put("fragment", "yes");

        attributes = context.asciiDoctor().parseAttributes(new URL(DOCUMENT_ATTRIBUTES_URL), attributes);

        context.getTmpDir().mkdirs();

        for (Versions.Version v : context.versions()) {
            try {
                URL url = new URL(BASE_URL + "release_notes/topics/" + v.getVersion().replace(".", "_") + ".adoc");

                String fileName = "release-notes-" + v.getVersion().replace(".", "_") + ".html";

                context.asciiDoctor().writeFile(attributes, url, context.getTmpDir(), fileName);

                v.setReleaseNotes("target/tmp/" + fileName);

                printStep("created", v.getVersion());
            } catch (FileNotFoundException e) {
                printStep("missing",  v.getVersion());
            } catch (Exception e) {
                printStep("error", v.getVersion() + " (" + e.getClass().getSimpleName() + ")");
            }
        }
    }

}
