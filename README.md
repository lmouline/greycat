<p align="center"><h1><b>GreyCat</b>: Many-World Temporal Graph</h1></p>
<p align="center"><img src="logo.png" /></p>
<p align="center"><h3>Empowering Next-Gen Live Analytics</h3></p>
The GreyCat escapes from the quantum box by analyzing all alternatives, now he is ready to do the same with your data.
For any questions please contact us via our Gitter:

[![Join the chat at https://gitter.im/datathings/greycat](https://badges.gitter.im/datathings/greycat.svg)](https://gitter.im/datathings/greycat?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/datathings/greycat.svg?branch=master)](https://travis-ci.org/datathings/greycat)

# Changelog

## Version 6 (11/04/2017)
    - Introducing Hash to check Remote Cache Consistency
    - New API for React Remote Graph usage
    - New API for Remote Task Context usage
    - New API for Stream Output of task (also in remote usage)
    - Fix various bugs according to new Arrays APIs.
    - Fix in Base64
    - NPE protection in Task API
    - Fix missing declaration of standard actions
    - add typed API to access complex elements such as Map, Relation...

## Version 5 (20/03/2017) 
    - New API for Arrays (now defined as complex sub objects such as Map or Relationships)
    - New Proxy approach to seamlessly manage temporal semantic of complex objects (Relationships...)
    - Temporary desactivation of OffHeap module (some bug remaining before the release)

## Version 4 (13/03/17)
    - Replaced declaration() in ActionRegistry and NodeRegistry by getOrCreateDeclaration. declaration() now acts like a simple get.
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
    
