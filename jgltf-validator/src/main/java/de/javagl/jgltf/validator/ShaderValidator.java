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

import de.javagl.jgltf.impl.GlTF;
import de.javagl.jgltf.impl.Shader;
import de.javagl.jgltf.model.GltfConstants;

/**
 * A class for validating {@link Shader}s
 */
class ShaderValidator extends AbstractGltfValidator
{
    /**
     * Default constructor
     * 
     * @param gltf The {@link GlTF} that contains the elements to validate
     */
    ShaderValidator(GlTF gltf)
    {
        super(gltf);
    }

    /**
     * Validate the given {@link Shader} ID, and return the
     * {@link ValidatorResult}
     * 
     * @param shaderId The {@link Shader} ID
     * @param expectedType The expected {@link Shader#getType() type}
     * @param currentContext The optional {@link ValidatorContext} describing 
     * where the given object appeared
     * @return The {@link ValidatorResult}
     */
    ValidatorResult validateShader(
        String shaderId, int expectedType, ValidatorContext currentContext)
    {
        ValidatorContext context = new ValidatorContext(currentContext)
            .with("shaders[" + shaderId + "]");
        ValidatorResult validatorResult = new ValidatorResult();
        
        // Validate the ID
        validatorResult.add(validateMapEntry(
            getGltf().getShaders(), shaderId, context));
        if (validatorResult.hasErrors())
        {
            return validatorResult;
        }
        
        Shader shader = getGltf().getShaders().get(shaderId);

        // Validate the shader.type
        Integer type = shader.getType();
        if (type == null)
        {
            validatorResult.addError(
                "The type is null", context);
            return validatorResult;
        }
        if (type != expectedType)
        {
            validatorResult.addError(
                "The type is not " + GltfConstants.stringFor(expectedType) + 
                ", but " + GltfConstants.stringFor(type), context);
            return validatorResult;
        }
        
        return validatorResult;
    }
    
}
