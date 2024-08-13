package objViewer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OBJLoader {
	public static Model loadModel(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		List<float[]> vertices = new ArrayList<>();
		List<float[]> normals = new ArrayList<>();
		List<Face> faces = new ArrayList<>();
		String line;

		while ((line = reader.readLine()) != null) {
			if (line.startsWith("v ")) {
				String[] tokens = line.split("\\s+");
				float x = Float.parseFloat(tokens[1]);
				float y = Float.parseFloat(tokens[2]);
				float z = Float.parseFloat(tokens[3]);
				vertices.add(new float[] { x, y, z });
			} else if (line.startsWith("vn ")) {
				String[] tokens = line.split("\\s+");
				float x = Float.parseFloat(tokens[1]);
				float y = Float.parseFloat(tokens[2]);
				float z = Float.parseFloat(tokens[3]);
				normals.add(new float[] { x, y, z });
			} else if (line.startsWith("f ")) {
				String[] tokens = line.split("\\s+");
				List<Integer> vertexIndices = new ArrayList<>();
				List<Integer> normalIndices = new ArrayList<>();

				for (int i = 1; i < tokens.length; i++) {
					String[] parts = tokens[i].split("/");
					int vertexIndex = Integer.parseInt(parts[0]) - 1; // Convertir a 0 basado en índice
					int normalIndex = parts.length > 2 ? Integer.parseInt(parts[2]) - 1 : -1; // Convertir a 0 basado en
																								// índice o -1 si no hay
																								// normal

					if (vertexIndex >= 0 && vertexIndex < vertices.size()) {
						vertexIndices.add(vertexIndex);
					} else {
						System.err.println("Índice de vértice fuera de rango: " + (vertexIndex + 1));
					}

					if (normalIndex >= 0 && normalIndex < normals.size()) {
						normalIndices.add(normalIndex);
					} else if (normalIndex != -1) {
						System.err.println("Índice de normal fuera de rango: " + (normalIndex + 1));
					}
				}

				// Crear la cara usando los índices validados
				if (vertexIndices.size() == 3) { // Asegúrate de que la cara tenga 3 vértices (triángulo)
					faces.add(new Face(vertexIndices.stream().mapToInt(Integer::intValue).toArray(),
							normalIndices.stream().mapToInt(Integer::intValue).toArray()));
				} else {
					System.err.println("Cara no es un triángulo o tiene un número incorrecto de vértices.");
				}
			}
		}
		reader.close();

		return new Model(vertices, normals, faces);
	}
}
