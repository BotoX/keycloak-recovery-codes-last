package kc.CredentialsOrder;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@JBossLog
public class CredentialsOrderEventListenerProviderFactory implements EventListenerProviderFactory {

    private static final String ID = "keycloak-recovery-codes-last";

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new CredentialsOrderEventListenerProvider(session);
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return ID;
    }
}