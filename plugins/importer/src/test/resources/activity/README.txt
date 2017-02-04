====
    Copyright 2017 The GreyCat Authors.  All rights reserved.
    <p>
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    <p>
    http://www.apache.org/licenses/LICENSE-2.0
    <p>
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
====

==================================================================
Activities of Daily Living (ADLs) Recognition Using Binary Sensors
Version 1.0
==================================================================
Francisco Javier Ord��ez
Office 2.1.B.17 (Sabatini Building)
Carlos III University of Madrid
Computer Science Deparment
Escuela Polit�cnica Superior
Avda. de la Universidad, 30. 28911 Legan�s. Madrid.  Spain.
==================================================================

This dataset comprises information regarding the ADLs performed by two users on a daily basis in their 
own homes. This dataset is composed by two instances of data, each one corresponding to a different 
user and summing up to 35 days of fully labelled data. Each instance of the dataset is described by 
three text files, namely: description, sensors events (features), activities of the daily living (labels). 
Sensor events were recorded using a wireless sensor network and data were labelled manually.

The dataset includes the following files:
=========================================

- 'README.txt'

- 'OrdonezA_Description.txt' - Setting configuration for user 'A'.

- 'OrdonezA_Sensors.txt' - Sensor events for user 'A'.

- 'OrdonezA_ADLs.txt' - Activity labels for user 'A'.

- 'OrdonezB_Description.txt' - Setting configuration for user 'B'.

- 'OrdonezB_Sensors.txt' - Sensor events for user 'B'.

- 'OrdonezB_ADLs.txt' Activity labels for user 'B'.

Notes: 
======
- Each sensor event and each activity label is a row on the corresponding text file.

- The format of this dataset is based on the data published by Tim van Kasteren et. al. To obtain further details about such dataset, visit https://sites.google.com/site/tim0306/.
  Both datasets share format and type of sensors, but not the collection of ADLs. However, the characteristics of these datasets are similar enough to be easily included in the same 
  experimentation setup.

- For further info about this dataset please contact: fordonez@inf.uc3m.es

License:
========
Use of this dataset in publications must be acknowledged by referencing the following publication:

	Ord��ez, F.J.; de Toledo, P.; Sanchis, A. Activity Recognition Using Hybrid Generative/Discriminative Models on Home Environments Using Binary Sensors. Sensors 2013, 13, 5460-5477.

This dataset is distributed AS-IS and no responsibility implied or explicit can be addressed to the authors or their institutions for its use or misuse. Any commercial use is prohibited.
Francisco Javier Ord��ez. November 2013.
