package prolod.common.models

case class MaintableObject(subjectId: Int,
                           propertyId: Int,
                           objectId: Int,
                           var internalLink: Option[Int] = None,
                           var datatypeId: Option[Int] = None,
                           var normalizedPattern: Option[Int] = None,
                           var pattern: Option[Int] = None,
                           var parsedValue: Option[Float] = None) {
}
