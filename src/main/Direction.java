package main;

public enum Direction {
	HORIZONTAL,
	VERTICAL;

	public Direction invert() {
		if(this == HORIZONTAL){
			return VERTICAL;
		}
		return HORIZONTAL;
	}
}
