FROM jenkins/jenkins:lts-jdk21

# ── Plugins ──────────────────────────────────────────────────────
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

# ── Node.js 22 (pour ng build) ──────────────────────────────────
USER root
RUN curl -fsSL https://deb.nodesource.com/setup_22.x | bash - \
    && apt-get install -y nodejs \
    && npm install -g @angular/cli \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# ── SonarScanner CLI ────────────────────────────────────────────
ARG SONAR_SCANNER_VERSION=6.2.1.4610
RUN curl -fsSL "https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-${SONAR_SCANNER_VERSION}-linux-x64.zip" \
        -o /tmp/sonar-scanner.zip \
    && unzip /tmp/sonar-scanner.zip -d /opt \
    && mv /opt/sonar-scanner-${SONAR_SCANNER_VERSION}-linux-x64 /opt/sonar-scanner \
    && ln -s /opt/sonar-scanner/bin/sonar-scanner /usr/local/bin/sonar-scanner \
    && rm /tmp/sonar-scanner.zip

# ── Répertoire JCasC ────────────────────────────────────────────
RUN mkdir -p /var/jenkins_home/casc_configs && chown jenkins:jenkins /var/jenkins_home/casc_configs

USER jenkins
