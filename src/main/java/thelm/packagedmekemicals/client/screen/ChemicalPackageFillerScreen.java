package thelm.packagedmekemicals.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;

import mekanism.api.chemical.ChemicalStack;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import thelm.packagedauto.client.screen.BaseScreen;
import thelm.packagedmekemicals.client.ChemicalRenderer;
import thelm.packagedmekemicals.menu.ChemicalPackageFillerMenu;
import thelm.packagedmekemicals.network.PacketHandler;
import thelm.packagedmekemicals.network.packet.SetChemicalAmountPacket;

public class ChemicalPackageFillerScreen extends BaseScreen<ChemicalPackageFillerMenu> {

	public static final ResourceLocation BACKGROUND = new ResourceLocation("packagedmekemicals:textures/gui/chemical_package_filler.png");
	public static final ChemicalRenderer CHEMICAL_RENDERER = new ChemicalRenderer(16, 52, 1);

	protected EditBox amountField;

	public ChemicalPackageFillerScreen(ChemicalPackageFillerMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@Override
	protected ResourceLocation getBackgroundTexture() {
		return BACKGROUND;
	}

	@Override
	protected void init() {
		clearWidgets();
		super.init();
		amountField = new EditBox(font, leftPos+30, topPos+57, 41, font.lineHeight, TextComponent.EMPTY);
		amountField.setBordered(false);
		amountField.setValue(String.valueOf(menu.blockEntity.requiredAmount));
		amountField.setTextColor(0xFFFFFF);
		amountField.setFilter(s->{
			if(s.isEmpty()) {
				return true;
			}
			try {
				int amount = Integer.parseInt(s);
				return amount >= 1 && amount <= 1000000;
			}
			catch(NumberFormatException e) {
				return false;
			}
		});
		amountField.setResponder(s->{
			try {
				int amount = Mth.clamp(Integer.parseInt(amountField.getValue()), 0, 1000000);
				if(amount != menu.blockEntity.requiredAmount) {
					PacketHandler.INSTANCE.sendToServer(new SetChemicalAmountPacket(amount));
					menu.blockEntity.requiredAmount = amount;
				}
			}
			catch(NumberFormatException e) {
				// NO OP
			}
		});
		addRenderableWidget(amountField);
	}

	@Override
	protected void renderBgAdditional(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
		blit(poseStack, leftPos+102, topPos+35, 176, 0, menu.blockEntity.getScaledProgress(22), 16);
		int scaledEnergy = menu.blockEntity.getScaledEnergy(40);
		blit(poseStack, leftPos+10, topPos+10+40-scaledEnergy, 176, 16+40-scaledEnergy, 12, scaledEnergy);
		if(menu.blockEntity.isWorking) {
			blit(poseStack, leftPos+102, topPos+30, 176, 61, 6, 5);
		}
		else {
			blit(poseStack, leftPos+102, topPos+30, 176, 56, 6, 5);
		}
		amountField.renderButton(poseStack, mouseX, mouseY, partialTicks);

		ChemicalStack<?> stack = menu.blockEntity.currentChemical.copy();
		if(!stack.isEmpty()) {
			stack.setAmount(menu.blockEntity.amount);
			CHEMICAL_RENDERER.render(poseStack, leftPos+80, topPos+17, stack, menu.blockEntity.requiredAmount);
		}
	}

	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		String s = menu.blockEntity.getDisplayName().getString();
		font.draw(poseStack, s, imageWidth/2 - font.width(s)/2, 6, 0x404040);
		font.draw(poseStack, menu.inventory.getDisplayName().getString(), menu.getPlayerInvX(), menu.getPlayerInvY()-11, 0x404040);
		if(mouseX-leftPos >= 10 && mouseY-topPos >= 10 && mouseX-leftPos <= 21 && mouseY-topPos <= 49) {
			renderTooltip(poseStack, new TextComponent(menu.blockEntity.getEnergyStorage().getEnergyStored()+" / "+menu.blockEntity.getEnergyStorage().getMaxEnergyStored()+" FE"), mouseX-leftPos, mouseY-topPos);
		}
		if(!menu.blockEntity.isWorking && mouseX-leftPos >= 102 && mouseY-topPos >= 30 && mouseX-leftPos <= 107 && mouseY-topPos <= 34) {
			renderTooltip(poseStack, new TranslatableComponent("block.packagedmekemicals.chemical_package_filler.redstone"), mouseX-leftPos, mouseY-topPos);
		}
		if(menu.blockEntity.isWorking && !menu.blockEntity.currentChemical.isEmpty() && mouseX-leftPos >= 80 && mouseY-topPos >= 17 && mouseX-leftPos <= 95 && mouseY-topPos <= 68) {
			renderTooltip(poseStack, new TextComponent("").append(menu.blockEntity.currentChemical.getTextComponent()).append(" "+menu.blockEntity.amount+" / "+menu.blockEntity.requiredAmount+" mB"), mouseX-leftPos, mouseY-topPos);
		}
		super.renderLabels(poseStack, mouseX, mouseY);
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		if(amountField.keyPressed(key, scanCode, modifiers)) {
			return true;
		}
		InputConstants.Key mouseKey = InputConstants.getKey(key, scanCode);
		if(minecraft.options.keyInventory.isActiveAndMatches(mouseKey) && amountField.isFocused()) {
			return true;
		}
		return super.keyPressed(key, scanCode, modifiers);
	}
}
