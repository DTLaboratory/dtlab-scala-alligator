package somind.dtlab.models
import java.time.ZonedDateTime

// particular type of a kind
final case class DtType(
    // the name of our type
    name: String,
    // the names of the properties (called props instead of attributes because
    // values of props can change - values of attributes cannot change)
    props: List[String],
    // datetime of creation - no updates allowed
    created: ZonedDateTime = ZonedDateTime.now()
)

// collection of all types in domain
final case class DtTypeMap(
    types: Map[String, DtType]
)

// todo: self-ref data structure for messaging actor tree (and support graph)
final case class Update()
