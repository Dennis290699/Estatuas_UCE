package objViewer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NORMAL_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glNormalPointer;
import static org.lwjgl.opengl.GL11.glVertexPointer;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

public class ModelInstance {
	Model model;
	float x, y, z;
	float rotationAngle = 0.0f; // Ángulo de rotación actual
	float rotationSpeed; // Velocidad de rotación en grados por frame
	float targetX, targetY, targetZ; // Coordenadas de destino
	float speed = 0.01f; // Velocidad de movimiento hacia el destino
	float startX, startY, startZ; // Coordenadas iniciales
	boolean movingToTarget = true; // Estado de movimiento: true si se mueve al destino, false si regresa al inicio
	private int vboId;
	int normalVboId;
	

	public ModelInstance(Model model, float startX, float startY, float startZ, float rotationSpeed, float targetx) {
		this.model = model;
		this.x = startX;
		this.y = startY;
		this.z = startZ;
		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;
		this.rotationSpeed = rotationSpeed;
		this.targetX = targetx;
		this.targetY = y;
		this.targetZ = z;
		createVBO();
	}

	private void createVBO() {
		List<float[]> vertices = model.vertices;
		List<float[]> normals = model.normals;
		List<Face> faces = model.faces;

		if (vertices.isEmpty()) {
			System.err.println("La lista de vértices está vacía.");
			return;
		}

		if (normals.isEmpty()) {
			System.err.println("La lista de normales está vacía.");
			return;
		}

		List<float[]> finalVertices = new ArrayList<>();
		List<float[]> finalNormals = new ArrayList<>();

		for (Face face : faces) {
			for (int i = 0; i < face.vertexIndices.length; i++) {
				int vertexIndex = face.vertexIndices[i];
				int normalIndex = face.normalIndices[i];

				// Validar índice del vértice
				if (vertexIndex >= 0 && vertexIndex < vertices.size()) {
					float[] vertex = vertices.get(vertexIndex);
					finalVertices.add(vertex);
				} else {
					System.err.println("Índice de vértice fuera de rango: " + (vertexIndex + 1));
					finalVertices.add(new float[] { 0.0f, 0.0f, 0.0f });
				}

				// Validar índice de la normal
				if (normalIndex >= 0 && normalIndex < normals.size()) {
					float[] normal = normals.get(normalIndex);
					finalNormals.add(normal);
				} else if (normalIndex != -1) {
					System.err.println("Índice de normal fuera de rango: " + (normalIndex + 1));
					finalNormals.add(new float[] { 0.0f, 0.0f, 1.0f });
				}
			}
		}

		// Convertir listas a buffers
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(finalVertices.size() * 3);
		FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(finalNormals.size() * 3);

		for (float[] vertex : finalVertices) {
			vertexBuffer.put(vertex);
		}
		vertexBuffer.flip();

		for (float[] normal : finalNormals) {
			normalBuffer.put(normal);
		}
		normalBuffer.flip();

		// Crear y cargar VBOs
		vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

		normalVboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, normalBuffer, GL15.GL_STATIC_DRAW);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public void render() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		glEnableClientState(GL_VERTEX_ARRAY);
		glVertexPointer(3, GL_FLOAT, 0, 0L);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalVboId);
		glEnableClientState(GL_NORMAL_ARRAY);
		glNormalPointer(GL_FLOAT, 0, 0L);

		glDrawArrays(GL_TRIANGLES, 0, model.faces.size() * 3);
		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_NORMAL_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public void update() {
		rotationAngle += rotationSpeed;
		if (rotationAngle > 360.0f) {
			rotationAngle -= 360.0f;
		}
		moveToTarget();
	}

	private boolean moveToTargetEnabled = false;

	public void toggleMoveToTarget() {
	    moveToTargetEnabled = !moveToTargetEnabled;
	}

	public void moveToTarget() {
	    if (!moveToTargetEnabled) return;
	    
	    float destinationX = movingToTarget ? targetX : startX;
	    float destinationY = movingToTarget ? targetY : startY;
	    float destinationZ = movingToTarget ? targetZ : startZ;

	    if (Math.abs(x - destinationX) > speed)
	        x += (destinationX - x) * speed;
	    if (Math.abs(y - destinationY) > speed)
	        y += (destinationY - y) * speed;
	    if (Math.abs(z - destinationZ) > speed)
	        z += (destinationZ - z) * speed;

	    if (Math.abs(x - destinationX) < speed && Math.abs(y - destinationY) < speed
	            && Math.abs(z - destinationZ) < speed) {
	        movingToTarget = !movingToTarget;
	    }
	}

}
