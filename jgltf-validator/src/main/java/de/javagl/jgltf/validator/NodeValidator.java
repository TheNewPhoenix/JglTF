/*
 * www.javagl.de - JglTF
 *
 * Copyright 2015-2016 Marco Hutter - http://www.javagl.de
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.jgltf.validator;

import java.util.List;

import de.javagl.jgltf.impl.GlTF;
import de.javagl.jgltf.impl.Node;

/**
 * A class for validating {@link Node}s
 */
class NodeValidator extends AbstractGltfValidator
{
    /**
     * The {@link MeshValidator}
     */
    private final MeshValidator meshValidator;
    
    /**
     * Default constructor
     * 
     * @param gltf The {@link GlTF} that contains the elements to validate
     */
    NodeValidator(GlTF gltf)
    {
        super(gltf);
        this.meshValidator = new MeshValidator(gltf);
    }

    /**
     * Validate the given {@link Node} ID, and return the
     * {@link ValidatorResult}
     * 
     * @param nodeId The {@link Node} ID
     * @param currentContext The optional {@link ValidatorContext} describing 
     * where the given object appeared
     * @return The {@link ValidatorResult}
     */
    ValidatorResult validateNode(
        String nodeId, ValidatorContext currentContext)
    {
        ValidatorContext context = new ValidatorContext(currentContext)
            .with("nodes[" + nodeId + "]");
        ValidatorResult validatorResult = new ValidatorResult();
        
        // Validate the ID
        validatorResult.add(validateMapEntry(
            getGltf().getNodes(), nodeId, context));
        if (validatorResult.hasErrors())
        {
            return validatorResult;
        }
        
        Node node = getGltf().getNodes().get(nodeId);
        
        // Validate the node.meshes
        List<String> meshes = node.getMeshes();
        if (meshes != null)
        {
            for (String meshId : meshes)
            {
                validatorResult.add(meshValidator.validateMesh(
                    meshId, context));
                if (validatorResult.hasErrors())
                {
                    return validatorResult;
                }
            }
        }
        
        // Validate the node.children
        List<String> children = node.getChildren();
        if (children != null)
        {
            for (String childNodeId : children)
            {
                validatorResult.add(validateNode(childNodeId, context));
                if (validatorResult.hasErrors())
                {
                    return validatorResult;
                }
            }
        }
        
        // Validate the node.camera
        String cameraId = node.getCamera();
        if (cameraId != null)
        {
            validatorResult.add(validateMapEntry(
                getGltf().getCameras(), cameraId, context.with("node.camera")));
            if (validatorResult.hasErrors())
            {
                return validatorResult;
            }
        }
        
        // Validate the node.skin
        String skinId = node.getSkin();
        if (skinId != null)
        {
            validatorResult.add(validateMapEntry(
                getGltf().getSkins(), skinId, context.with("node.skin")));
            if (validatorResult.hasErrors())
            {
                return validatorResult;
            }
        }
        
        // Validate the node.skeletons
        List<String> skeletons = node.getSkeletons();
        if (skeletons != null && !skeletons.isEmpty())
        {
            if (skinId == null)
            {
                validatorResult.addError(
                    "Node has skeletons: " + skeletons + ", but no skin",
                    context);
                return validatorResult;
            }
            //Skin skin = getGltf().getSkins().get(skinId);

            for (int i = 0; i < skeletons.size(); i++)
            {
                String skeleton = skeletons.get(i);
                validatorResult.add(validateNode(
                    skeleton, context.with("node.skeletons["+i+"]")));
                if (validatorResult.hasErrors())
                {
                    return validatorResult;
                }
                
                /* TODO Skins are not explicitly validated yet
                Node skeletonNode = getGltf().getNodes().get(skeleton);
                String jointName = skeletonNode.getJointName();
                */
            }
        }
        
        return validatorResult;
    }
    
}
