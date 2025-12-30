import { Application } from 'express';
import axios from 'axios';

export default function (app: Application): void {
  app.get('/', async (req, res) => {
    try {
      const response = await axios.get('http://localhost:8090/task/v1/find-all');
      console.log(response.data);
      const tasks = response.data.map((task: any) => ({
        ...task,
        status: toTitleCase(task.status)
      }));

      res.render('home', { tasks });
    } catch (error) {
      console.error('Error fetching tasks:', error);
      res.render('home', { tasks: [] });
    }
  });

app.post('/task/create', async (req, res, next) => {
  const { title, description, dueDateTime } = req.body;
  const status = 'CREATED';

  try {
    await axios.post('http://localhost:8090/task/v1/create', {
      title,
      description,
      status,
      dueDateTime
    });

    res.redirect('/');
  } catch (err) {
    next(err);
  }
});

  app.get('/delete-task/:id', async (req, res, next) => {
  const { id } = req.params;
  try {
    await axios.delete(`http://localhost:8090/task/v1/delete/${id}`);
    res.redirect('/');
  } catch (error) {
    console.error('Error deleting task:', error);
    next(error);
  }
});

app.get('/task/edit/:id', async (req, res, next) => {
  const { id } = req.params;

  try {
    const response = await axios.get(`http://localhost:8090/task/v1/find/${id}`);
    res.render('edit', { task: response.data });
  } catch (err) {
    next(err);
  }
});

app.post('/task/edit/:id', async (req, res, next) => {
  const { id } = req.params;
  const { title, description, status, dueDateTime } = req.body;

  try {
    await axios.put(`http://localhost:8090/task/v1/update/${id}`, {
      title,
      description,
      status,
      dueDateTime
    });

    res.redirect('/');
  } catch (err) {
    next(err);
  }
});

app.get('/task/view/:id', async (req, res, next) => {
  const { id } = req.params;

  try {
    const response = await axios.get(`http://localhost:8090/task/v1/find/${id}`);
    const task = {
      ...response.data,
      status: toTitleCase(response.data.status)
    }
    res.render('view', { task});
  } catch (err) {
    next(err);
  }
});

}

function toTitleCase(status: string): string {
  return status
    .toLowerCase()                
    .replace(/_/g, ' ')           
    .replace(/\b\w/g, c => c.toUpperCase());
}