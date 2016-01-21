# About the Knowledge Management Dashboard

This Nuxeo plugin uses [Nuxeo Data Visualzation](https://doc.nuxeo.com/x/WZCRAQ) to create a dashboard for the CVS demo.

# Requirements

The nuxeo-km Studio project (internal) is required. The build will work, but the runtime will fail if don't have the correct document types.

See [CORG/Compiling Nuxeo from sources](http://doc.nuxeo.com/x/xION)

Building this sample requires the following software:

- [Node.js] (http://nodejs.org)
- [Bower] (http://bower.io)

# Building

Navigate into the folder that contains `pom.xml`.

    mvn clean install

The plugin will be placed in the `target` folder.

# Deploying

It is recommended to deploy the marketplace package. Just move to the parent folder of this repository and `mvn clean install`

# Usage

Once installed in the the Knowledge Management demo, there is a bundled Web application at `http://yourserver/nuxeo/cvs-dashboard`.

# Resources

## Reporting issues

Contact [jfletcher@nuxeo.com](mailto:jfletcher@nuxeo.com)

# Licensing

[GNU Lesser General Public License (LGPL) v2.1](http://www.gnu.org/licenses/lgpl-2.1.html)

# About Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Netflix, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at [www.nuxeo.com](http://www.nuxeo.com).
