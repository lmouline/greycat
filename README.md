GreyCat documentation is coming soon ... stay tuned !
==================

For any questions please contact us via our Gitter:

[![Join the chat at https://gitter.im/datathings/greycat](https://badges.gitter.im/datathings/greycat.svg)](https://gitter.im/datathings/greycat?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/datathings/greycat.svg?branch=master)](https://travis-ci.org/datathings/greycat)

# Changelog

## Version 4 (planned for 03/03/17)
    - fix free bug in flat task action
    - define interfaces for Blas plugin
    - cleanup build scripts
    - implement multi-get within RocksDB plugin
    
## Version 3 (27/02/17)
    - fix apriori dirty element on get
    - fix dirty flag after remote state chunk load *(need global check)*
    - fix dirty flag after remote timetree chunk load *(need global check)*
    - switch test execution with full NPM management
    - use J2TS-JUNIT (https://www.npmjs.com/search?q=j2ts-junit)
    - rename sub test package as greycatTest to avoid conflict for NPM
    - new RocksDB support for ARM-V7 processor
    - closing issue #3 add TypeScript correct typing header to NPM
    - alignement of interface NodeState, StateChunk and Node (all inherit from container)
    
## Version 2 (17/02/17)
    - introduce new NPM packaging (https://www.npmjs.com/package/greycat)
    - use J2TS-JRE (https://www.npmjs.com/search?q=j2ts-junit)
    - rename base package as greycat
    - closing #2

## Version 1
    - first release with the GreyCat name, mostly renamed from ManyWorldGraph project
    
