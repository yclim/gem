# Group Extract Monitor

# Quick Start
#### Start React App backed by mock api
```bash
cd app
yarn install //  typically only need to run it once only... or when modules are added/removed
yarn dev
```

# Setup
### Frontend development
* Install node and yarn
    * Node Version: v12.16.1
    * Yarn Version: v1.22.4
* Prettier format on save
    * ensure prettier and file watcher plugin is installed & follow instruction from [here](https://prettier.io/docs/en/webstorm.html)
* Live Reload
    * Install Live Reload plugin (e.g [for Chrome](https://chrome.google.com/webstore/detail/livereload/jnihajbhpnppcggbcgedagnkighmdlei?hl=en)) on your browser for yarn watch to work

### Backend development
* start a yarn watch to "compile" the frontend scripts

```bash
cd app
yarn install //  typically only need to run once
yarn watch //  this compiles, watches and moves your distribution to the tomcat hosted folder
```
* run GemApplication
      * the app will run in default tomcat port 8080
* open Firefox/Chrome and go to http://localhost:8080


### Generate Mock Sample files
Generate samples to output directory (default: target/samples, 20 files for each type)
```bash
mvn package
java -cp target/gem-0.1-SNAPSHOT.jar -Dloader.main=innohack.gem.filegen.GenerateMockFiles org.springframework.boot.loader.PropertiesLauncher [</path/to/output-dir> <numOfFiles>]
```

### Devops
Jenkins: [Go!](https://tinyurl.com/y8n27x3z)