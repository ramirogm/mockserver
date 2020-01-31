package org.mockserver.netty.integration.mock;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.cli.Main;
import org.mockserver.client.MockServerClient;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.socket.PortFactory;
import org.mockserver.socket.tls.KeyStoreFactory;
import org.mockserver.testing.integration.mock.AbstractMockingIntegrationTestBase;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockserver.configuration.ConfigurationProperties.*;
import static org.mockserver.echo.tls.NonMatchingX509KeyManager.invalidClientSSLContext;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.HttpStatusCode.OK_200;
import static org.mockserver.stop.Stop.stopQuietly;

/**
 * @author jamesdbloom
 */
public class ClientAuthenticationDynamicCAMockingIntegrationTest extends AbstractClientAuthenticationMockingIntegrationTest {

    private static final int severHttpPort = PortFactory.findFreePort();

    @BeforeClass
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void startServer() throws IOException {
        tlsMutualAuthenticationRequired(true);
        dynamicallyCreateCertificateAuthorityCertificate(true);
        File temporaryDirectory = new File(File.createTempFile("random", "temp").getParent() + UUID.randomUUID().toString());
        temporaryDirectory.mkdirs();
        directoryToSaveDynamicSSLCertificate(temporaryDirectory.getAbsolutePath());
        Main.main("-serverPort", "" + severHttpPort);

        mockServerClient = new MockServerClient("localhost", severHttpPort).withSecure(true);
    }

    @AfterClass
    public static void stopServer() {
        stopQuietly(mockServerClient);
        tlsMutualAuthenticationRequired(false);
        dynamicallyCreateCertificateAuthorityCertificate(false);
        directoryToSaveDynamicSSLCertificate("");
    }

    @Override
    public int getServerPort() {
        return severHttpPort;
    }

}