package com.github.game.util;

public class Camera {

	private float distance = 50;
	private float angle = 0;

	float pitch = 20;
	float yaw = 0;

	public float x = 0;
	public float y = 0;
	public float z = 0;

	public void update(float px, float pz, float py, int zoom, boolean up, boolean down, boolean right, boolean left) {
		this.distance -= (zoom * 0.1f);

		if(up || down) {
			this.pitch += Utils.ternary(up, .8f);
			this.pitch = Utils.clamp(pitch, 22.5f, 67.5f);//this.pitch < 22.5f ? 22.5f : this.pitch > 67.5f ? 67.5f : this.pitch;
		}

		if(right || left) {
			this.angle += Utils.ternary(right, 1.6f);
		}
		
		float pitch = Utils.toRadians(this.pitch);
		float angle = Utils.toRadians(this.angle);
		
		float horizontalDistance = this.distance * Utils.cos(pitch);
		float verticalDistance = this.distance * Utils.sin(pitch);
		
		this.x = px - horizontalDistance * Utils.sin(angle);

		this.z = pz - horizontalDistance * Utils.cos(angle);
		
		this.y = py + verticalDistance;

		this.yaw = 180 - this.angle;
	}
}
