package alpvax.globalchests.gui;

import alpvax.globalchests.inventory.ContainerSynched;
import alpvax.globalchests.inventory.ItemHandlerSynched;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiInventorySynched extends GuiContainer
{
	/** The ResourceLocation containing the chest GUI texture. */
	private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

	/** window height is calculated with these values; the more rows, the heigher */
	private final int inventoryRows;

	public GuiInventorySynched(EntityPlayer player, ItemHandlerSynched inv)
	{
		super(new ContainerSynched(player, inv));
		this.allowUserInput = false;
		this.inventoryRows = inv.getSlots() / 9;
		this.ySize = 114 + this.inventoryRows * 18;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
		this.drawTexturedModalRect(i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
	}

}
