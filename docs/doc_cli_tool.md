# Klutter CLI tool

The Klutter CLI is a small utility to generate a new Klutter project.

# Getting started

Download the CLI tool: "https://github.com/buijs-dev/klutter/blob/v2022-pre-alpha-4/klutter-cli/cli.zip"

Unzip the file. Move to folder cli/bin. Run:

```shell
./klutter create
```

This generates a Klutter project in the folder where the CLI tool is unzipped.

# Options
- [location](#Location)
- [app-id](#AppId)
- [project-name](#ProjectNam)

### Location
Use <b>--location</b> to specify an absolute project path:

```shell
./klutter create --location /Users/sidious/projects/
```

When not specified the default location is the root folder of the CLI tool (so the folder where the zip is extracted).

### AppId
Use <b>--app-id</b> to specify the application id for the app, where the last part of the id will be used for the app name:

```shell
./klutter create --app-id dev.empire.apps.jeditrackingapp
```

The app will be named jeditrackingapp. 
When not specified the default appId is com.example.example.

### ProjectName
Use <b>--project-name</b> to specify the name of the project, meaning the root folder name of the Klutter project:

```shell
./klutter create --project-name order66
```

The app will be named jeditrackingapp.
When not specified the default projectName is example.

### Putting it all together

The following command will create a klutter project in the directory <b>/Users/sidious/projects/order66</b>
with an app named <b>jeditrackingapp</b>, beloning to the org <b>dev.empire.apps</b>.

```shell
./klutter create --project-name order66 --app-id dev.empire.apps.jeditrackingapp --location /Users/sidious/projects/
```