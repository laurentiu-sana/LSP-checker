#!/bin/bash

# Compile the system
ant

truncate --size 0 log/scoring_algorithm.txt
truncate --size 0 log/debug.txt

ant run -Dconfig.file=config/savdemoprecond.properties
ant run -Dconfig.file=config/savdemopostcond.properties

#ant run -Dconfig.file=config/jdom.properties
#ant run -Dconfig.file=config/jdk.properties
exit

ant run -Dconfig.file=config/postconditions.properties
ant run -Dconfig.file=config/sav.properties
ant run -Dconfig.file=config/savadvanced.properties

ant run -Dconfig.file=config/accounts.properties
ant run -Dconfig.file=config/cars.properties
ant run -Dconfig.file=config/constraints.properties
ant run -Dconfig.file=config/dummy.properties
ant run -Dconfig.file=config/numeric.properties
ant run -Dconfig.file=config/sorting.properties
ant run -Dconfig.file=config/stateful.properties
ant run -Dconfig.file=config/stateless.properties
ant run -Dconfig.file=config/features.properties


