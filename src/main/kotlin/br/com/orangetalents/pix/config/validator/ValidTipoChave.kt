package br.com.orangetalents.pix.config.validator

import br.com.orangetalents.pix.dto.RegistraChavePixRequestDto
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [ValidTipoChaveValidator::class])
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CLASS
)
annotation class ValidTipoChave(
    val message: String = "tipo de (\${validatedValue.tipoDeChave}) inválido",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
)

class ValidTipoChaveValidator : ConstraintValidator<ValidTipoChave, RegistraChavePixRequestDto> {
    override fun isValid(
        value: RegistraChavePixRequestDto?,
        annotationMetadata: AnnotationValue<ValidTipoChave>,
        context: ConstraintValidatorContext
    ): Boolean {
        if (value?.tipoDeChave == null) return true

        return value.tipoDeChave.validate(value.chavePix)
    }
}
