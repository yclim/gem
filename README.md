# Group Extract Monitor

# Quick Start
#### Start React App backed by mock api
```bash
cd app
yarn install //typically only need to run it once only... or when modules are added/removed
yarn dev:mock
```

# Setup
#### Frontend development
* Install node and yarn
    * Node Version: v12.16.1
    * Yarn Version: v1.22.4
* Prettier format on save
    * ensure prettier and file watcher plugin is installed & follow instruction from https://prettier.io/docs/en/webstorm.html
    
### Backend development
* start a yarn watch to "compile" the frontend scripts

```bash
cd app
yarn install //typically only need to run once
yarn mock yarn watch //this compiles, watches and moves your distribution to the tomcat hosted folder
```
* run GemApplication
      * the app will run in default tomcat port 8080
* open Firefox/Chrome and go to http://localhost:8080