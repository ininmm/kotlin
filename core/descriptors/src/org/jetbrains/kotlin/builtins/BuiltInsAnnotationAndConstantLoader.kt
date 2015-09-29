/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.builtins

import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationWithTarget
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.serialization.ProtoBuf
import org.jetbrains.kotlin.serialization.builtins.BuiltInsProtoBuf
import org.jetbrains.kotlin.serialization.deserialization.*
import org.jetbrains.kotlin.types.JetType

class BuiltInsAnnotationAndConstantLoader(
        module: ModuleDescriptor
) : AnnotationAndConstantLoader<AnnotationDescriptor, ConstantValue<*>, AnnotationWithTarget> {
    private val deserializer = AnnotationDeserializer(module)

    override fun loadClassAnnotations(
            classProto: ProtoBuf.Class,
            nameResolver: NameResolver
    ): List<AnnotationDescriptor> {
        val annotations = classProto.getExtension(BuiltInsProtoBuf.classAnnotation).orEmpty()
        return annotations.map { proto -> deserializer.deserializeAnnotation(proto, nameResolver) }
    }

    override fun loadCallableAnnotations(
            container: ProtoContainer,
            proto: ProtoBuf.Callable,
            nameResolver: NameResolver,
            kind: AnnotatedCallableKind
    ): List<AnnotationWithTarget> {
        val annotations = proto.getExtension(BuiltInsProtoBuf.callableAnnotation).orEmpty()
        return annotations.map { proto -> AnnotationWithTarget(deserializer.deserializeAnnotation(proto, nameResolver), null) }
    }

    override fun loadValueParameterAnnotations(
            container: ProtoContainer,
            callable: ProtoBuf.Callable,
            nameResolver: NameResolver,
            kind: AnnotatedCallableKind,
            parameterIndex: Int,
            proto: ProtoBuf.Callable.ValueParameter
    ): List<AnnotationDescriptor> {
        val annotations = proto.getExtension(BuiltInsProtoBuf.parameterAnnotation).orEmpty()
        return annotations.map { proto -> deserializer.deserializeAnnotation(proto, nameResolver) }
    }

    override fun loadExtensionReceiverParameterAnnotations(
            container: ProtoContainer,
            callable: ProtoBuf.Callable,
            nameResolver: NameResolver,
            kind: AnnotatedCallableKind
    ): List<AnnotationDescriptor> = emptyList()

    override fun loadTypeAnnotations(proto: ProtoBuf.Type, nameResolver: NameResolver): List<AnnotationDescriptor> {
        // TODO: support type annotations in built-ins
        return listOf()
    }

    override fun loadPropertyConstant(
            container: ProtoContainer,
            proto: ProtoBuf.Callable,
            nameResolver: NameResolver,
            expectedType: JetType
    ): ConstantValue<*>? {
        val value = proto.getExtension(BuiltInsProtoBuf.compileTimeValue)
        return deserializer.resolveValue(expectedType, value, nameResolver)
    }
}
