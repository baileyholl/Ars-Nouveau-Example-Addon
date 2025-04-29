package com.c446.ars_trinkets;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import com.c446.ars_trinkets.registry.ModRegistry;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.ModConfigSpec;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
;

public class Util {
    ArrayList<Item> focus = new ArrayList<>();

    public static void CreateParticleBeam(Vec3 start, Vec3 end, ServerLevel level, ParticleColor color) {
        /**
         * Creates a beam of particles between two points.
         * @param start: Vec3; the vector that represents the start of the line.
         * @param end: Vec3;  the vector that represents the end of the line.
         * @param color: ParticleColor; the particle color of the beam.
         * This function was copied and modified from the Too Many Glyphs project. Github Repo here : https://github.com/DerringersMods/TooManyGlyphs/blob/1.19.x/src/main/java/io/github/derringersmods/toomanyglyphs/common/network/PacketRayEffect.java#L29.
         * */

        double distance = start.distanceTo(end);
        double traceStart = 0.0, increment = 1.0 / 16;
        for (double d = traceStart; d < distance; d += increment) {
            double fractionalDistance = d / distance;
            double speedCoefficient = Mth.lerp(fractionalDistance, 0.2, 0.001);
            level.addParticle(
                    GlowParticleData.createData(color),
                    Mth.lerp(fractionalDistance, start.x, end.x),
                    Mth.lerp(fractionalDistance, start.y, end.y),
                    Mth.lerp(fractionalDistance, start.z, end.z),
                    (level.random.nextFloat() - 0.5) * speedCoefficient,
                    (level.random.nextFloat() - 0.5) * speedCoefficient,
                    (level.random.nextFloat() - 0.5) * speedCoefficient);
        }
    }


    public static void CreateParticleBeam(Vec3 start, Vec3 end, ServerLevel level, SimpleParticleType particle) {
        /**
         * Creates a beam of particles between two points.
         * @param start: Vec3; the vector that represents the start of the line.
         * @param end: Vec3;  the vector that represents the end of the line.
         * @param color: ParticleColor; the particle color of the beam.
         * @param increment: double; how many blocks to increment the beam by.
         * This function was copied and modified from the Too Many Glyphs project. Github Repo here : https://github.com/DerringersMods/TooManyGlyphs/blob/1.19.x/src/main/java/io/github/derringersmods/toomanyglyphs/common/network/PacketRayEffect.java#L29.
         * */

        double distance = start.distanceTo(end);
        double traceStart = 0.0, increment = 1.0 / 16;
        for (double d = traceStart; d < distance; d += increment) {
            double fractionalDistance = d / distance;
            level.sendParticles(particle,
                    Mth.lerp(fractionalDistance, start.x, end.x),
                    Mth.lerp(fractionalDistance, start.y, end.y),
                    Mth.lerp(fractionalDistance, start.z, end.z)
                    , 100, 0, 0, 0, 1);
        }
    }

    public static void CreateParticleBeam(Vec3 start, Vec3 end, ServerLevel level, SimpleParticleType particle, double increment) {
        /**
         * Creates a beam of particles between two points.
         * @param start: Vec3; the vector that represents the start of the line.
         * @param end: Vec3;  the vector that represents the end of the line.
         * @param color: ParticleColor; the particle color of the beam.
         * @param increment: double; how many blocks to increment the beam by.
         * This function was copied and modified from the Too Many Glyphs project. Github Repo here : https://github.com/DerringersMods/TooManyGlyphs/blob/1.19.x/src/main/java/io/github/derringersmods/toomanyglyphs/common/network/PacketRayEffect.java#L29.
         * */


        double distance = start.distanceTo(end);
        double traceStart = 0.0;
        for (double d = traceStart; d < distance; d += increment) {
            double fractionalDistance = d / distance;
            level.sendParticles(particle,
                    Mth.lerp(fractionalDistance, start.x, end.x),
                    Mth.lerp(fractionalDistance, start.y, end.y),
                    Mth.lerp(fractionalDistance, start.z, end.z)
                    , 100, 0, 0, 0, 1);
        }
    }

    public static void CreateParticleBeam(Vec3 start, Vec3 end, ServerLevel level, SimpleParticleType particle, double increment, int count) {
        /**
         * Creates a beam of particles between two points.
         * @param start: Vec3; the vector that represents the start of the line.
         * @param end: Vec3;  the vector that represents the end of the line.
         * @param color: ParticleColor; the particle color of the beam.
         * @param increment: double; how many blocks to increment the beam by.
         * This function was copied and modified from the Too Many Glyphs project. Github Repo here : https://github.com/DerringersMods/TooManyGlyphs/blob/1.19.x/src/main/java/io/github/derringersmods/toomanyglyphs/common/network/PacketRayEffect.java#L29.
         * */


        double distance = start.distanceTo(end);
        double traceStart = 0.0;
        for (double d = traceStart; d < distance; d += increment) {
            double fractionalDistance = d / distance;
            level.sendParticles(particle,
                    Mth.lerp(fractionalDistance, start.x, end.x),
                    Mth.lerp(fractionalDistance, start.y, end.y),
                    Mth.lerp(fractionalDistance, start.z, end.z)
                    , count, 0, 0, 0, 1);
        }
    }

    public static long randomLongFromRange(int lower, int upper) {
        return (lower + (long) (Math.random() * (upper - lower)));
    }
}

