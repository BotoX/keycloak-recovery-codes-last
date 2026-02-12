package unilogin;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.events.Event;
import org.keycloak.events.EventType;
import org.keycloak.events.Details;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.utils.DefaultRequiredActions;
import org.keycloak.credential.CredentialModel;

@JBossLog
public class CredentialsOrderEventListenerProvider implements EventListenerProvider {

    private final KeycloakSession session;

    public CredentialsOrderEventListenerProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        if (event.getType() != EventType.UPDATE_CREDENTIAL) {
            return;
        }

        var customRequiredAction = event.getDetails().get(Details.CUSTOM_REQUIRED_ACTION);
        if (customRequiredAction == null) {
            return;
        }

        if (!(customRequiredAction.equals(DefaultRequiredActions.Action.CONFIGURE_TOTP.getAlias())
                || customRequiredAction.equals(DefaultRequiredActions.Action.WEBAUTHN_REGISTER.getAlias()))) {
            return;
        }

        var realm = session.realms().getRealm(event.getRealmId());
        var user = session.users().getUserById(realm, event.getUserId());

        var recoveryCodesCredential = user.credentialManager()
                .getCredentials()
                .filter(credential -> "recovery-authn-codes".equals(credential.getType()))
                .findFirst()
                .orElseThrow();

        String moveID = null;
        for (CredentialModel credential : user.credentialManager().getCredentials().toList()) {
            if (recoveryCodesCredential != credential && credential.getId() != null) {
                moveID = credential.getId();
            }
        }

        if (moveID != null) {
            user.credentialManager().moveStoredCredentialTo(recoveryCodesCredential.getId(), moveID);
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
    }

    @Override
    public void close() {
    }
}
