package guis;

import org.lwjgl.util.vector.Matrix4f;

import shaders.ShaderProgram;

public class GuiShader extends ShaderProgram{
    
    private static final String filename = "guis/gui";
     
    private int location_transformationMatrix;
 
    public GuiShader() {
        super(filename);
    }
     
    public void loadTransformation(Matrix4f matrix){
        super.loadMatrix4f(location_transformationMatrix, matrix);
    }
 
    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "inPosition");
    }
}
