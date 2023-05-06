package vapourdrive.agricultural_enhancements.modules.manager;

import net.minecraft.world.inventory.ContainerData;

public class CropManagerData implements ContainerData {
    public enum Data {
        FUEL,
        FERTILIZER
    }

    private final int[] data = {0,0};

    public int get(Data data) {
        return this.get(data.ordinal());
    }

    @Override
    public int get(int index) {
        if (index >= data.length || index < 0) {
            return 0;
        } else {
            return data[index];
        }
    }

    public void set(Data data, int value) {
        this.set(data.ordinal(), value);
    }

    @Override
    public void set(int index, int value) {
        if (index < data.length && index >= 0) {
            data[index] = value;
        }
    }

    @Override
    public int getCount() {
        return data.length;
    }

}
