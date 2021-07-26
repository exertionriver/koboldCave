# koboldCave
Demonstration of probabilistically-generated oakLeaf and nodeMesh using Korge Multiplatform Kotlin Game Engine

Built using IntelliJ Community Edition 2021.1.2 / Kotlin Plugin 1.5.20 on Ubuntu 20.04

And using Korge 2.1.1.6 (Apache 2.0 License) Korge Project template

Repo found at: https://github.com/korlibs/korge

Example usage, after cloning / downloading:

```./gradlew :runJvm```

Hope this may help you in your development work. - IanP

v0.5 - bottom-up, top-down nodeMesh elaboration, simple navigation

![Simple Navigation](https://user-images.githubusercontent.com/13192685/126921656-bce9fd21-9637-403c-8306-9aac2a6acbac.mp4)

![Top-down elaboration](https://user-images.githubusercontent.com/13192685/126921652-4d4a586b-1cb2-41a2-a24b-0ff46151695a.mp4) (bordering node meshes sourced from a centroid mesh)

![Bottom-up elaboration](https://user-images.githubusercontent.com/13192685/126921632-1f843da4-e165-4f32-9c9e-b69ff2a798ff.mp4) (centroid mesh defined by bordering node meshes)

v0.4.1 - cleaning up bordering algorithm

v0.4 - bordering functionality for leaf, lace, lattice, nodeLine, nodeMesh; top-down centroid rooms

Leaf bordering a simple nodemesh (borders shown)
![v0 4_leaf_bordering](https://user-images.githubusercontent.com/13192685/125235014-05ce0f80-e29f-11eb-9e94-2e5e38764048.png)

Room meshes bordering out from the center mesh
![v0 4_roomMesh_border](https://user-images.githubusercontent.com/13192685/125235012-05357900-e29f-11eb-99a5-6aebae12e136.png)

Room meshes generated from centroid mesh
![v0 4_centroidRooms](https://user-images.githubusercontent.com/13192685/125235010-049ce280-e29f-11eb-9ee5-6766215a7613.png)

v0.3 - presenting various line demos with navigable UI, adding lace and lattice

v0.2 - refactoring leaves and nodes, nodeline with noise, k-means clustering

v0.1 - modelling organic cave-like environment rooms network
