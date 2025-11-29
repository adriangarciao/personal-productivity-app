Migration Notes
===============

This document explains the breaking API changes introduced in the
`feature/add-openapi-pagination-ci` branch and how clients can migrate.

Summary of breaking changes
---------------------------
- `GET /persons` now returns a paginated response (`Page<PersonDto>`) instead of a raw JSON array.
- `GET /tasks` now returns a paginated response (`Page<TaskDto>`) instead of a raw JSON array.

Why this change
----------------
Switching to pageable responses keeps API responses predictable for large data sets
and provides useful metadata such as `totalElements`, `totalPages`, and sorting information.

New response shape
------------------
Instead of returning a top-level array, endpoints now return an object similar to Spring Data's
`Page<T>` JSON representation. Example:

{
  "content": [ /* actual DTO objects */ ],
  "pageable": { /* pagination info */ },
  "totalElements": 42,
  "totalPages": 3,
  "last": false,
  "size": 20,
  "number": 0,
  "sort": { /* sort info */ },
  "first": true,
  "numberOfElements": 20
}

How to request pages
--------------------
Use the standard Spring `Pageable` query parameters:

- `page` (0-based page index)
- `size` (page size)
- `sort` (e.g. `sort=dueDate,desc`)

Examples
--------
- First page, 10 items per page:

  GET /persons?page=0&size=10

- Sort tasks by due date descending, second page (page index 1):

  GET /tasks?page=1&size=20&sort=dueDate,desc

Migration tips
--------------
- If you previously iterated over the root JSON array, switch to `response.content`.
- If you only need all items and don't care about paging, you can request a large `size`, but
  be cautious of memory and performance for very large datasets.

Temporary compatibility
-----------------------
If you require a non-breaking transition, consider exposing a legacy alias endpoint such as
`/persons/list` or `/tasks/list` that returns the old array shape. I can add these endpoints
on request.

Questions or issues
-------------------
If any client breaks after this change, open an issue or request the legacy alias and I'll add it.
