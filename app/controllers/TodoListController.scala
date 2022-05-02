package controllers

  import javax.inject.Inject

  import play.api.mvc._

  import javax.inject.Singleton

  import play.api.libs.json._

  import scala.collection.mutable

  import models.TodoListItem
  import models.NewTodoListItem

@Singleton
class TodoListController @Inject()(val controllerComponents: ControllerComponents)
extends BaseController {
    private val todoList = new mutable.ListBuffer[TodoListItem]()

    todoList += TodoListItem(1, "test", true)
    todoList += TodoListItem(2, "some other value", false)

    implicit val todoListJson = Json.format[TodoListItem]

    def getAll(): Action[AnyContent] = Action {
        if (todoList.isEmpty) {
            NoContent
        } else {
            Ok(Json.toJson(todoList))
        }
    }

    def getById(itemId: Long) = Action {
        val foundItem = todoList.find(_.id == itemId)
        foundItem match {
            case Some(item) => Ok(Json.toJson(item))
            case None => NotFound
        }
    }

    implicit val NewTodoListItem = Json.format[NewTodoListItem]

    def addNewItem() = Action { implicit request =>
        val content = request.body
        val jsonObject = content.asJson
        val todoListItem: Option[NewTodoListItem] =
            jsonObject.flatMap(
                Json.fromJson[NewTodoListItem](_).asOpt
            )

        todoListItem match {
            case Some(newItem) =>
                val nextId = todoList.map(_.id).max + 1
                val toBeAdded = TodoListItem(nextId, newItem.description, false)
                todoList += toBeAdded
                Created(Json.toJson(toBeAdded))
            case None =>
                BadRequest
        }
    }
}