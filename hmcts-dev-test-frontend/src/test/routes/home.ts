import axios from "axios";
import express, { Application } from "express";
import routes from "../../main/routes/home";

jest.mock("axios");
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe("Task Routes", () => {
  let app: Application;
  let req: any;
  let res: any;
  let next: any;

  beforeEach(() => {
    app = express();
    routes(app);

    req = {};
    res = {
      render: jest.fn(),
      redirect: jest.fn()
    };
    next = jest.fn();
  });

  function getRoute(path: string, method: string = "get") {
    const layer = (app as any)._router.stack.find(
      (l: any) => l.route && l.route.path === path && l.route.methods[method]
    );
    return layer.route.stack[0].handle;
  }

  it("GET / should render tasks with title-cased status", async () => {
    mockedAxios.get.mockResolvedValue({
      data: [
        { id: 1, title: "Test", status: "IN_PROGRESS" }
      ]
    });

    const handler = getRoute("/");
    await handler(req, res);

    expect(res.render).toHaveBeenCalledWith("home", {
      tasks: [
        { id: 1, title: "Test", status: "In Progress" }
      ]
    });
  });

  it("GET / should render empty tasks on error", async () => {
    mockedAxios.get.mockRejectedValue(new Error("Network error"));

    const handler = getRoute("/");
    await handler(req, res);

    expect(res.render).toHaveBeenCalledWith("home", { tasks: [] });
  });

  it("POST /task/create should call axios.post and redirect", async () => {
    mockedAxios.post.mockResolvedValue({});

    req.body = {
      title: "New Task",
      description: "Desc",
      dueDateTime: "2025-01-01T10:00"
    };

    const handler = getRoute("/task/create", "post");
    await handler(req, res, next);

    expect(mockedAxios.post).toHaveBeenCalledWith(
      "http://localhost:8090/task/v1/create",
      {
        title: "New Task",
        description: "Desc",
        status: "CREATED",
        dueDateTime: "2025-01-01T10:00"
      }
    );

    expect(res.redirect).toHaveBeenCalledWith("/");
  });

  it("GET /delete-task/:id should delete and redirect", async () => {
    mockedAxios.delete.mockResolvedValue({});
    req.params = { id: "123" };

    const handler = getRoute("/delete-task/:id");
    await handler(req, res, next);

    expect(mockedAxios.delete).toHaveBeenCalledWith(
      "http://localhost:8090/task/v1/delete/123"
    );
    expect(res.redirect).toHaveBeenCalledWith("/");
  });

  it("GET /task/edit/:id should render edit page", async () => {
    mockedAxios.get.mockResolvedValue({
      data: { id: 1, title: "Test" }
    });
    req.params = { id: "1" };

    const handler = getRoute("/task/edit/:id");
    await handler(req, res, next);

    expect(res.render).toHaveBeenCalledWith("edit", {
      task: { id: 1, title: "Test" }
    });
  });

  it("POST /task/edit/:id should update and redirect", async () => {
    mockedAxios.put.mockResolvedValue({});
    req.params = { id: "1" };
    req.body = {
      title: "Updated",
      description: "Updated desc",
      status: "DONE",
      dueDateTime: "2025-01-01T10:00"
    };

    const handler = getRoute("/task/edit/:id", "post");
    await handler(req, res, next);

    expect(mockedAxios.put).toHaveBeenCalledWith(
      "http://localhost:8090/task/v1/update/1",
      {
        title: "Updated",
        description: "Updated desc",
        status: "DONE",
        dueDateTime: "2025-01-01T10:00"
      }
    );

    expect(res.redirect).toHaveBeenCalledWith("/");
  });

  it("GET /task/view/:id should render view page with title-cased status", async () => {
    mockedAxios.get.mockResolvedValue({
      data: { id: 1, title: "Test", status: "IN_PROGRESS" }
    });
    req.params = { id: "1" };

    const handler = getRoute("/task/view/:id");
    await handler(req, res, next);

    expect(res.render).toHaveBeenCalledWith("view", {
      task: { id: 1, title: "Test", status: "In Progress" }
    });
  });
});