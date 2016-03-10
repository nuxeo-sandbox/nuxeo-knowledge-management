# About nuxeo-knowledge-management

A plug-in for the [Nuxeo](http://nuxeo.com) Knowledge Management Example.


## List of Features

- A custom Elasticsearch mapping
- Admin UI to configure scoring parameters
- Admin UI to configure the list of synonyms
- A custom dashboard showing stats on queries, documents, ...
- Custom front-end, written with WebComponents

The NuxeoPackage installs a set the plug-ins (.jar), and also sets up a template, named `es-for-nuxeo-km`, in `nuxeo-knowledge-management/nuxeo-km-mp/src/main/resources/install/templates/`. This template is very important, since it contains the elasticsearch mapping and also some files requests at runtime: A script to "boost" regions and a first synonyms file. See below how to install them (must be done manually)

## Build

[![Build Status](https://qa.nuxeo.org/jenkins/buildStatus/icon?job=Sandbox/sandbox_nuxeo-knowledge-management-master)](https://qa.nuxeo.org/jenkins/view/sandbox/job/Sandbox/job/sandbox_nuxeo-knowledge-management-master/)

There are a few steps in order to testa nd build the plug-in. Basically, each part using WebComponents (typically, the frontend) must be setup frist, then the whole package can be built.


The plug-in depends on `nuxeo-es-synonyms` (you can find it [here](https://github.com/nuxeo-sandbox/nuxeo-es-synonyms)). Because, at the time this README is written, this plug-in is not available in the maven repositories of nuxeo, you must first build it on your computer:

To build `nuxeo-es-synonyms`, and assuming maven is correctly setup on your computer:
```
cd /path/to/some/directory
git clone https://github.com/nuxeo-sandbox/nuxeo-es-synonyms
mvn install
```

Now, when building `nuxeo-knowledge-management`, the NuxeoPackage declares a dependency to `nuxeo-es-synonyms` and the .jars of `nuxeo-es-synonyms` will be automatically added (there is no need to install `nuxeo-es-synonyms`).

To build the plug-in, and assuming maven is correctly setup on your computer:

```
git clone
mvn clean install
```

The NuxeoPackage is now located at /nuxeo-knowledge-management/nuxeo-km-mp/target/nuxeo-km-mp-{version}.zip and can be installed on your server.


## Install

### Install the NuxeoPackage
Install the package (nuxeo-km-mp-{version}.zip) on your instance (see the "Offline" mode of [this documentation page](https://doc.nuxeo.com/x/moFH))

### Install the Specific Elasticsearch Files on your Server

Also, 2 important files must be copied in the elastic search folder of your server: `region_boost.groovy` and `synonyms.txt`. After having installed the NuxeoPackage, you will find these files in the `templates/es-for-nuxeo-km` folder.

So, for example, on Linux with Elasticsearch installed at /etc/elasticsearch:

```
# Copy the first list of synonyms
sudo cp TEMPLATE_FOLDER/es-for-nuxeo-km/config/synonyms.txt /etc/elasticsearch/synonyms.txt
# Allow the nuxeo user to read/write it
sudo chmod 666 /etc/elasticsearch/synonyms.txt

# Create the scripts folder if it did not already exist
sudo mkdir /etc/elasticsearch/scripts
# Copy the region_boost script
sudo cp TEMPLATE_FOLDER/es-for-nuxeo-km/config/scripts/region_boost.groovy /etc/elasticsearch/scripts
```


## Testing the Frontend on your Localhost

This part is useful if you want to test the frontend without embedding it in the server, so it acts as an external client. Notice that the plug-in still has to be installed on the server.

### Quick-start (for experienced users)

With Node.js installed, run the following one liner from the root of your Polymer Starter Kit download:

```
npm install -g gulp bower
npm install
bower install
```

### Prerequisites (for everyone)

The full starter kit requires the following major dependencies:

- Node.js, used to run JavaScript tools from the command line.
- npm, the node package manager, installed with Node.js and used to install Node.js packages.
- gulp, a Node.js-based build tool.
- bower, a Node.js-based package manager used to install front-end packages (like Polymer).

**To install dependencies: (from command line)**

1)  Check your Node.js version.

```
node --version
```

The version should be at or above 4.2.3.

2)  If you don't have Node.js installed, or you have a lower version, go to [nodejs.org](https://nodejs.org) and click on the big green Install button.

3)  Install `gulp` and `bower`.

```
npm install -g gulp
npm install bower
```

This lets you run `gulp` and `bower` from the command line.

4)  Install the app's local `npm` and `bower` dependencies.

```
cd /path/to/nuxeo-km-frontend
npm install
bower install
```

This installs the element sets (Paper, Iron, Platinum) and tools the plugins requires to build and serve apps.

Now, to test as an external client:

1. Update `nuxeo-km-frontend/src/main/app/index.html` and change `<nuxeo-connection url="/nuxeo"></nuxeo-connection>` so it uses the correct distant server address and also contains the login and the password
2. Run the application:

  ```
  cd /path/to/nuxeo-km-frontend
  gulp serve
  ```


## Prerequisites

Besides the dependence on `nuxeo-es-synonyms` described above:

* The plugin depends on a dedicated Studio Project, and assumes some specific Document Types exist


## Support

**These features are not part of the Nuxeo Production platform.**

These solutions are provided for inspiration and we encourage customers to use them as code samples and learning resources.

This is a moving project (no API maintenance, no deprecation process, etc.) If any of these solutions are found to be useful for the Nuxeo Platform in general, they will be integrated directly into platform, not maintained here.


## Licensing

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)


## About Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Netflix, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris.

More information is available at [www.nuxeo.com](http://www.nuxeo.com).
