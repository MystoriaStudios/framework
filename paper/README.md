# Framework Paper Module

## Brainstorm:

- We can make a Data Watcher wrapper utilizing build and supply that wrapper with a handler in the nms modules as a
  solution for multi version data watchers.
- For that we can add a constant or some sort of flags system for them to make it a little more type safe. possibly an
  enum?
- Clean up the NMS Modules both code wise and how they are organized, maybe even making an annotations for the modules
  with a like `@NMSMenuHandler(Version.1_17_R1)` or something
- Write `AbstractFlag` system for regions
- Write a metric system using influx or promotheus db.
- Write a per player regon system for blocks and entities that works with stuff like schematics.
- Make `DisguiseInfo` provider and make it abstract.