package cubesnake;

public class CubeMatrix {
	public static final int DOTS_LENGTH = 512;
	public static final int CACHE_LENGTH = 64;

	private boolean[] dot;

	public CubeMatrix() {
		dot = new boolean[DOTS_LENGTH];
	}

	public boolean[] getDot() {
		return dot;
	}

	public boolean getDot(int index) {
		try {
			return dot[index];
		} catch (ArrayIndexOutOfBoundsException ex) {
			return false;
		}
	}

	public void setDot(int index, boolean val) {
		try {
			dot[index] = val;
		} catch (ArrayIndexOutOfBoundsException ex) {
			//
		}
	}

	public void setDot(int x, int y, int z, boolean val) {
		try {
			int index = getIndex(x, y, z);
			dot[index] = val;
		} catch (ArrayIndexOutOfBoundsException ex) {
			//
		}
	}
	
	public byte[] getCache() {
		byte[] cache = new byte[CACHE_LENGTH];

		for (int i = 0; i < dot.length; i++) {
			int index = i / 8;
			if (dot[i])
				cache[index] |= 0x01 << (i % 8);
		}
		
		return cache;
	}

	private int getIndex(int x, int y, int z) {
		return (64 * z + 8 * y + x);
	}
}
