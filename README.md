# GEM
App to help build rules to group structurally similar files together.

# Quick Start
start webapp at localhost:8080
```bash
mvn spring-boot:run -P prod
```

# Setup
* Install node and yarn
    * Node Version: v12.16.1
    * Yarn Version: v1.22.4
* Refer to [guide](https://github.com/yclim/gem/wiki/Setup-Auto-Code-Formatting-on-Intellij) to setup auto code formatting
* For `yarn watch` to work, Install Live Reload plugin (e.g [for Chrome](https://chrome.google.com/webstore/detail/livereload/jnihajbhpnppcggbcgedagnkighmdlei?hl=en)) on your browser

### Generate Mock Sample files
Generate samples to output directory (default: target/samples, 20 files for each type)
```bash
mvn package
java -cp target/gem-0.1-SNAPSHOT.jar -Dloader.main=innohack.gem.filegen.GenerateMockFiles org.springframework.boot.loader.PropertiesLauncher [</path/to/output-dir> <numOfFiles>]
```

Alternatively in IDE
* run `innohack.gem.filegen.GenerateMockFiles`
* the output will be in target/samples

### Devops
Jenkins: [Go!](https://tinyurl.com/y8n27x3z)