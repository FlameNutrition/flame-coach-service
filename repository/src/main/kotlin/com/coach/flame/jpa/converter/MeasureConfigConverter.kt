package com.coach.flame.jpa.converter

import com.coach.flame.jpa.entity.MeasureConfig
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class MeasureConfigConverter : AttributeConverter<MeasureConfig, String> {

    override fun convertToDatabaseColumn(attribute: MeasureConfig): String {
        return attribute.code
    }

    override fun convertToEntityAttribute(dbData: String): MeasureConfig {
        return MeasureConfig.valueOf(dbData)
    }

}
