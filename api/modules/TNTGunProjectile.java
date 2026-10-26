package ru.loper.suntnt.api.modules;

import lombok.Generated;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;

public class TNTGunProjectile {
   private final Block block;
   private TNTPrimed tntPrimed;

   @Generated
   public Block getBlock() {
      return this.block;
   }

   @Generated
   public TNTPrimed getTntPrimed() {
      return this.tntPrimed;
   }

   @Generated
   public void setTntPrimed(TNTPrimed tntPrimed) {
      this.tntPrimed = tntPrimed;
   }

   @Generated
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof TNTGunProjectile)) {
         return false;
      } else {
         TNTGunProjectile other = (TNTGunProjectile)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$block = this.getBlock();
            Object other$block = other.getBlock();
            if (this$block == null) {
               if (other$block != null) {
                  return false;
               }
            } else if (!this$block.equals(other$block)) {
               return false;
            }

            Object this$tntPrimed = this.getTntPrimed();
            Object other$tntPrimed = other.getTntPrimed();
            if (this$tntPrimed == null) {
               if (other$tntPrimed != null) {
                  return false;
               }
            } else if (!this$tntPrimed.equals(other$tntPrimed)) {
               return false;
            }

            return true;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof TNTGunProjectile;
   }

   @Generated
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $block = this.getBlock();
      result = result * 59 + ($block == null ? 43 : $block.hashCode());
      Object $tntPrimed = this.getTntPrimed();
      result = result * 59 + ($tntPrimed == null ? 43 : $tntPrimed.hashCode());
      return result;
   }

   @Generated
   public String toString() {
      String var10000 = String.valueOf(this.getBlock());
      return "TNTGunProjectile(block=" + var10000 + ", tntPrimed=" + String.valueOf(this.getTntPrimed()) + ")";
   }

   @Generated
   public TNTGunProjectile(Block block) {
      this.block = block;
   }
}
