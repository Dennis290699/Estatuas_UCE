package objViewer;

import java.util.List;

public class Model {
	List<float[]> vertices;
    List<float[]> normals;
    public List<Face> faces;

    public Model(List<float[]> vertices, List<float[]> normals, List<Face> faces) {
        this.vertices = vertices;
        this.normals = normals;
        this.faces = faces;
    }
    
    
}
