package com.mcmoddev.sprucewillisthexmastree.common.init;

import com.google.common.collect.Sets;
import com.mcmoddev.sprucewillisthexmastree.SpruceWillisTheXmasTree;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = SpruceWillisTheXmasTree.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@SuppressWarnings("unused")
public class EventHandler {

	private static Collection<Biome> biomes = null;
	private static Biome.SpawnListEntry entry = null;

	@SubscribeEvent
	@SuppressWarnings("unused")
	public static void onLoad(ModConfig.Loading event) {
		if (event.getConfig().getSpec() != WillisConfig.SERVER_SPECIFICATION) {
			return;
		}

		if (entry != null) {
			biomes.stream().map(biome -> biome.getSpawns(EntityClassification.MONSTER)).forEach(list ->
				list.remove(entry));
			biomes = Collections.emptyList();
		}

		if (WillisConfig.SERVER.SpruceWillisSpawnNaturally.get()) {
			int currentWeight = WillisConfig.SERVER.SpruceWillisSpawnWeight.get();

			if (currentWeight > 0) {
				biomes = ForgeRegistries.BIOMES.getValues();
				if (WillisConfig.SERVER.BiomeWhitelist.get() != null && WillisConfig.SERVER.BiomeWhitelist.get().size() > 0) {
					Set<String> whitelist = Sets.newHashSet(WillisConfig.SERVER.BiomeWhitelist.get());
					biomes = biomes.stream().filter(b ->
						whitelist.contains(b.getRegistryName().toString())).collect(Collectors.toList());
				} else {
					if (WillisConfig.SERVER.BiomeBlacklist.get() != null && WillisConfig.SERVER.BiomeBlacklist.get().size() > 0) {
						Set<String> blacklist = Sets.newHashSet(WillisConfig.SERVER.BiomeBlacklist.get());
						biomes = biomes.stream().filter(b ->
							!blacklist.contains(b.getRegistryName().toString())).collect(Collectors.toList());
					}
				}

				entry = new Biome.SpawnListEntry(WillisEntityRegistry.SPRUCE_WILLIS_THE_XMAS_TREE,
					WillisConfig.SERVER.SpruceWillisSpawnWeight.get(), 1, 5);
				biomes.stream().map(biome -> biome.getSpawns(EntityClassification.MONSTER)).forEach(list ->
					list.add(entry));
			}
		}
	}
}
