#!/bin/csh -f
#=================================================================
# BUILDING SCRIPT for COATJAVA PROJECT (first maven build)
# then the documentatoin is build from the sources and commited
# to the documents page
#=================================================================
# Maven Build

if(`filetest -e lib` == '0') then
    mkdir lib
endif

# Elastic
echo "Building Elastic..."
    cd Elastic
    mvn install
    mvn package
    cp target/Elastic-1.0.jar ../lib/
    cd ..

# JPSI-Plots
echo "Building JPSI-Plots..."
    cd JPSI-Plots
    mvn install
    mvn package
    cp target/JPSI-Plots-1.0.jar ../lib/
    cd ..


# Finishing touches
echo ""
echo "--> Done building....."
echo ""
echo "    Usage : build.sh"
echo ""
