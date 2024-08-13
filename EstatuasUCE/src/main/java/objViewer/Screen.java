package objViewer;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.IOException;
import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Screen {
	private long window;
	private List<ModelInstance> modelInstances = new ArrayList<>();
	private float cameraX = 0.0f;
	private float cameraY = -5.0f;
	private float cameraZ = -5.0f;
	private float cameraYaw = 0.0f;
	private float cameraPitch = 0.0f;

	// ID del VBO para el plano
	private int planeVBO;

	public void run() {
		try {
			init();
			loop();
			glfwFreeCallbacks(window);
			glfwDestroyWindow(window);
		} finally {
			glfwTerminate();
			glfwSetErrorCallback(null).free();
		}
	}

	private void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		window = glfwCreateWindow(1280, 720, "Estatuas UCE - Presiona ESC para salir", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);
		GL.createCapabilities();

		glEnable(GL_DEPTH_TEST);
		setupLights(); // Configura las luces
		setupMaterials(); // Configura materiales
		loadModels();
		setupPlane(); // Configura el plano

		glfwSetKeyCallback(window, this::keyCallback);
	}

	private void loadModels() {
	    try {
	        // Distancia inicial en el eje x
	        float initialX = 5.0f;
	        // Incremento en el eje x para cada modelo
	        float incrementX = 12.0f;
	        // Posición constante en el eje y
	        float yPosition = 0.0f;
	        // Posición constante en el eje z
	        float zPosition = -10.0f;

	        for (int i = 1; i <= 21; i++) {
	            // Carga el modelo correspondiente
	            Model model = OBJLoader.loadModel("src/main/resources/models/" + i + ".obj");
	            // Añade el modelo a la lista con las posiciones incrementadas
	            modelInstances.add(new ModelInstance(model, initialX + (i - 1) * incrementX, yPosition, zPosition, 1f, i * 1));
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private void setupLights() {
		glEnable(GL_LIGHTING); // Habilita la iluminación

		// Configuración de luz global
		float[] ambientLight = { 0.2f, 0.2f, 0.2f, 1.0f };
		float[] diffuseLight = { 0.8f, 0.8f, 0.8f, 1.0f };
		float[] specularLight = { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] lightPosition = { 0.0f, 0.0f, 1.0f, 0.0f };

		glLightfv(GL_LIGHT0, GL_AMBIENT, ambientLight);
		glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuseLight);
		glLightfv(GL_LIGHT0, GL_SPECULAR, specularLight);
		glLightfv(GL_LIGHT0, GL_POSITION, lightPosition);

		glEnable(GL_LIGHT0); // Habilita la primera fuente de luz

	}

	private void setupMaterials() {
		float[] ambientMaterial = { 0.2f, 0.2f, 0.2f, 1.0f };
		float[] diffuseMaterial = { 0.8f, 0.8f, 0.8f, 1.0f };
		float[] specularMaterial = { 1.0f, 1.0f, 1.0f, 1.0f };
		float shininess = 50.0f;

		glMaterialfv(GL_FRONT, GL_AMBIENT, ambientMaterial);
		glMaterialfv(GL_FRONT, GL_DIFFUSE, diffuseMaterial);
		glMaterialfv(GL_FRONT, GL_SPECULAR, specularMaterial);
		glMaterialf(GL_FRONT, GL_SHININESS, shininess);
	}

	private void setupPlane() {
	    float[] vertices = {
	        // Coordenadas del plano
	        -300.0f, 0.0f, -300.0f,
	        300.0f, 0.0f, -300.0f,
	        300.0f, 0.0f, 300.0f,
	        -300.0f, 0.0f, 300.0f
	    };

	    FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
	    vertexBuffer.put(vertices).flip();

	    planeVBO = glGenBuffers();
	    glBindBuffer(GL_ARRAY_BUFFER, planeVBO);
	    glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
	}

	private void processInput() {
	    if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
	        cameraZ += 0.5f;
	    }
	    if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
	        cameraZ -= 0.5f;
	    }
	    if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
	        cameraX -= 0.5f;
	    }
	    if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
	        cameraX += 0.5f;
	    }
	    if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS) {
	        cameraPitch += 1.0f;
	    }
	    if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS) {
	        cameraPitch -= 1.0f;
	    }
	    if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS) {
	        cameraYaw -= 1.0f;
	    }
	    if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS) {
	        cameraYaw += 1.0f;
	    }
	}


	private void keyCallback(long window, int key, int scancode, int action, int mods) {
	    if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
	        glfwSetWindowShouldClose(window, true);
	    }

	    // Agrega una tecla para activar/desactivar el movimiento
	    if (key == GLFW_KEY_M && action == GLFW_PRESS) {
	        for (ModelInstance instance : modelInstances) {
	            instance.toggleMoveToTarget();
	        }
	    }
	}


	private void loop() {
	    glClearColor(0.1f, 0.1f, 0.1f, 1.0f); // Color de fondo oscuro

	    setupProjectionMatrix();
	    while (!glfwWindowShouldClose(window)) {

	        processInput();
	        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	        setupViewMatrix();

	        renderPlane(); // Renderiza el plano

	        float[] lightPos = { 0.0f, 10.0f, 10.0f, 1.0f }; // Posición de la luz
	        float[] plane = { 0.0f, 1.0f, 0.0f, 0.0f }; // Plano en y=0
	        float[] shadowMatrix = createShadowMatrix(lightPos, plane);

	        // Renderiza las sombras en rojo
	        glDisable(GL_LIGHTING); // Desactiva la iluminación para renderizar sombras
	        for (ModelInstance instance : modelInstances) {
	            renderShadow(instance, shadowMatrix);
	        }
	        glEnable(GL_LIGHTING); // Vuelve a activar la iluminación

	        // Renderiza los modelos normales
	        glColor3f(1.0f, 1.0f, 1.0f); // Restablece el color a blanco
	        for (ModelInstance instance : modelInstances) {
	            instance.update();
	            glPushMatrix();
	            glTranslatef(instance.x, instance.y, instance.z);
	            glRotatef(instance.rotationAngle, 0.0f, 1.0f, 0.0f); // Cambiado a eje y para rotar correctamente
	            instance.render();
	            glPopMatrix();
	        }

	        glfwSwapBuffers(window);
	        glfwPollEvents();
	    }
	}



	private void renderPlane() {
	    glEnableClientState(GL_VERTEX_ARRAY);
	    glBindBuffer(GL_ARRAY_BUFFER, planeVBO);
	    glVertexPointer(3, GL_FLOAT, 0, 0);
	    glDrawArrays(GL_QUADS, 0, 4);
	    glDisableClientState(GL_VERTEX_ARRAY);
	}

	private void setupProjectionMatrix() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		float aspectRatio = (float) 1280 / 720;
		float fov = 50.0f; // Ángulo de visión más amplio
		float nearPlane = 0.1f;
		float farPlane = 1000.0f;
		float y_scale = (float) (1 / Math.tan(Math.toRadians(fov / 2)));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = farPlane - nearPlane;

		FloatBuffer projectionMatrix = BufferUtils.createFloatBuffer(16);
		projectionMatrix
				.put(new float[] { x_scale, 0, 0, 0, 0, y_scale, 0, 0, 0, 0, -((farPlane + nearPlane) / frustum_length),
						-1, 0, 0, -((2 * nearPlane * farPlane) / frustum_length), 0 });
		projectionMatrix.flip();
		glLoadMatrixf(projectionMatrix);
	}

	private void setupViewMatrix() {
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glRotatef(cameraPitch, 1.0f, 0.0f, 0.0f);
		glRotatef(cameraYaw, 0.0f, 1.0f, 0.0f);
		glTranslatef(cameraX, cameraY, cameraZ);
	}
	
	private float[] createShadowMatrix(float[] lightPos, float[] plane) {
	    float[] shadowMatrix = new float[16];
	    float dot = plane[0] * lightPos[0] + plane[1] * lightPos[1] + plane[2] * lightPos[2] + plane[3] * lightPos[3];

	    shadowMatrix[0]  = dot - lightPos[0] * plane[0];
	    shadowMatrix[4]  = 0.0f - lightPos[0] * plane[1];
	    shadowMatrix[8]  = 0.0f - lightPos[0] * plane[2];
	    shadowMatrix[12] = 0.0f - lightPos[0] * plane[3];

	    shadowMatrix[1]  = 0.0f - lightPos[1] * plane[0];
	    shadowMatrix[5]  = dot - lightPos[1] * plane[1];
	    shadowMatrix[9]  = 0.0f - lightPos[1] * plane[2];
	    shadowMatrix[13] = 0.0f - lightPos[1] * plane[3];

	    shadowMatrix[2]  = 0.0f - lightPos[2] * plane[0];
	    shadowMatrix[6]  = 0.0f - lightPos[2] * plane[1];
	    shadowMatrix[10] = dot - lightPos[2] * plane[2];
	    shadowMatrix[14] = 0.0f - lightPos[2] * plane[3];

	    shadowMatrix[3]  = 0.0f - lightPos[3] * plane[0];
	    shadowMatrix[7]  = 0.0f - lightPos[3] * plane[1];
	    shadowMatrix[11] = 0.0f - lightPos[3] * plane[2];
	    shadowMatrix[15] = dot - lightPos[3] * plane[3];

	    return shadowMatrix;
	}
	
	private void renderShadow(ModelInstance instance, float[] shadowMatrix) {
	    glPushMatrix();
	    glMultMatrixf(shadowMatrix);
	    glTranslatef(instance.x, instance.y, instance.z);
	    glRotatef(instance.rotationAngle, 0.0f, 1.0f, 0.0f);
	    glColor3f(1.0f, 0.0f, 0.0f); // Color rojo para la sombra
	    instance.render();
	    glPopMatrix();
	}



}
