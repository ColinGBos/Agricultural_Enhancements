# Welcome to Agricultural Enhancements

Requires [VapourWare](https://modrinth.com/mod/vapourware) as of 1.20+

## Overview

This is a mod that consists of thoughtfully content around agriculture including harvesting, planting, irrigation, soil management and more. This mod is the result of finally getting around to implementing ideas up to a decade old. While there are some features that you can find elsewhere, this mod should feel welcome in almost any pack or play-through and balances well against the vanilla experience. The mod is mostly documented in-game through the use of tooltips and JEI. Enjoy a rich experience of managing your farms in Minecraft!

## Items

### Farmer's Wrench

This wrench can be used to rotate anything that can be rotated (not tested with every block, results may vary) and is used to disassemble machines. Some machines have modes or functions that are interacted with using the wrench.

### Watering Can

This item holds one bucket of water and can be used to slowly encourage crop growth and irrigate soil.

## Machines

All machines have a variety of config options; be sure to check the server config file if you're interested in changing these. All machines also require some degree of fuel which is stored in an internal reserve that varies in size between machines. Punching machines with the Farmer's Wrench is the best way to disassemble without losing fuel or other resources.

### Irrigation Controller

This machine pressurizes networks of irrigation pipes up to 15 blocks away. It requires a water source block (any block that registers as a vanilla source of Water Fluid) underneath it but does not actively interact with the block. Pipes can be connected either above the machine or behind it. Sprayers can be placed no closer than 3 blocks away from each other and interact with crops and soil in a 3x3 area a configurable height below them.

### Fertilizer Producer

This machine breaks down ingredients into macro-nutrients and creates Fertilizer when enough of each nutrient is available. The fertilizer that is produced follows a NPK ratio of 1:1:1. Various resources from vanilla and mods have been added to break down into macro-nutrients but if you have suggestions for others please let me know.
It is important to balance the inventory of macro-nutrients; adding too many of one ingredient may jam the machine and require venting with the Farmer's Wrench. Ingredients will only be broken down if there is room for all outputs.

### Crop Manager

This machine has a range of 9 blocks away from itself. It can till the ground into Tilled Soil when crouch-using the wrench on it and when it has some fuel. It will plant a variety of seeds, saplings, and more. It also interacts with soil and keeps nutrients up with fertilizer - it does this more effectively than can be done manually.

### Harvester

This machine harvests in a range of up to 9 blocks away from itself. It can harvest either destructively (break blocks and leave air), or non-destructively (reap drops and change the growth down to 0); this mode is not available for everything - netherwart and anything that is a 'bushblock' may not work. The mode can be enabled in the config and can be toggled with the Farmer's Wrench. An item can be used to modify the loot generation, i.e. silk touch and fortune. Note. the item is not consumed (there may be a config option for this to be changed in the future if desired).

## Non-machine Blocks

### Tilled Soil

A more immersive variation of farmland. Soil has nutrient and moisture values which encourage crop growth - more nutrients and moisture result in faster growth with nutrients having a larger impact. Moisture passes through soil up to 5 blocks away from a water source and will also increase when it's raining. Biomes with higher precipitation have higher base moisture. Nutrients can be increased by using fertilizer on the crops or soil but is more efficiently managed with the Crop Manager. Nutrients are consumed slowly when crops grow on top of the soil. Falling on Tilled Soil won't turn it to Soil but will soft-trample crops, reverting growth to 0 but not uprooting the crops.

### Soil

If you place a block above Tilled Soil, it turns into Soil; the nutrients and moisture are retained and moisture can still pass to Soil blocks and Tilled Soil blocks. Re-till to plant more crops.

## Integration

### Jade

Jade integration is provided for most Machines, Tilled Soil and Soil, and Irrigation systems.

### JEI

JEI integration includes recipes for what can be planted with the Crop Manager (Item and the resulting Block's name), recipes for Fertilizer in the Fertilizer Producer, and information about the various blocks and items.
