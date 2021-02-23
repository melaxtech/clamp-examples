# clamp-examples

This repository contains api examples of the CLAMP software (https://clamp.uth.edu/)
To debug and run, you will need to contact the CLAMP team for a valid license (http://www.melaxtech.com/)

License file needs to be put in resources folder with following directory structure `clamp/license/control/`

##Scripts description
- `DemoDocumentParser.java` demonstrates how to parse and retrieve necessary information from processed by pipeline XMI document.

- `DemoTokenizer.java` demonstrates how to detect sentences and tokens from raw text files.

- `DemoEntitiyRecognizer.java` demonstrates available functionality to load pipeline from JAR file, process document by 
pipeline, show recognized named entities, and save the processed files in XMI format.