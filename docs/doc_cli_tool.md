# Klutter CLI tool

The Klutter CLI is a small utility to generate a new Klutter project.

# Getting started

Download the CLI tool: "https://github.com/buijs-dev/klutter/blob/v2022-pre-alpha-4/klutter-cli/cli.zip"

Unzip the file. Move to folder cli/bin. Run:

```shell
./cli
```

This generates a Klutter project in the folder where the CLI tool is unzipped.

# Options
- [location](#Location)
- [name](#Name)

### Location
Use <b>--location</b> to specify an absolute project path:

```shell
./cli --location /Users/sidious/projects/order66
```

### Name
Use <b>--name</b> to specify a name for the project (and app):

```shell
./cli --name JediTrackingApp
```