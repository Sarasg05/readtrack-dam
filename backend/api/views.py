from django.http import JsonResponse
from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated
import json

from .models import AnnualGoal, Author, Genre, Book, Reading, ReadingSession
from .serializers import (
    AnnualGoalSerializer,
    AuthorSerializer,
    GenreSerializer,
    BookSerializer,
    ReadingSerializer,
    ReadingSessionSerializer
)


@csrf_exempt
def books(request):

    if request.method == 'GET':
        books = Book.objects.all()

        response = []
        for b in books:
            response.append({
                'id': b.id,
                'title': b.title,
                'author': b.author.name,
                'total_pages': b.total_pages,
                'synopsis': b.synopsis,
                'genres': [g.name for g in b.genres.all()]
            })

        return JsonResponse(response, safe=False)

    elif request.method == 'POST':
        body = json.loads(request.body)

        if body.get('title') is None:
            return JsonResponse({'error': 'Missing title'}, status=400)

        book = Book.objects.create(
            title=body['title'],
            author_id=body['author'],
            total_pages=body['total_pages'],
            synopsis=body.get('synopsis', '')
        )

        book.genres.set(body.get('genres', []))

        return JsonResponse({'id': book.id}, status=201)

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def book_by_id(request, id):
    try:
        book = Book.objects.get(id=id)
    except Book.DoesNotExist:
        return JsonResponse({'error': 'Not found'}, status=404)

    if request.method == 'GET':
        return JsonResponse({
            'id': book.id,
            'title': book.title,
            'author': book.author.name,
            'genres': [g.name for g in book.genres.all()]
        })

    elif request.method == 'PUT':
        body = json.loads(request.body)

        book.title = body.get('title',book.title)
        book.total_pages = body.get('total_pages', book.total_pages)
        book.save()

        return JsonResponse({'updated': True})

    elif request.method == 'DELETE':
        book.delete()
        return JsonResponse({'deleted': True})

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

class AnnualGoalViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = AnnualGoal.objects.all()
    serializer_class = AnnualGoalSerializer

    def get_queryset(self):
        user = self.request.user
        return AnnualGoal.objects.filter(user=user)

@csrf_exempt
def annual_goals(request):
    if request.method == 'GET':
        user_id = request.GET.get('user', None)

        readings = AnnualGoal.objects.all()
        if user_id:
            annual_goals = readings.filter(user_id=user_id)

        response = []
        for a in annual_goals:
            response.append([{
                'id': a.id,
                'year': a.year,
                'target_books': a.target_books
            }])

        return JsonResponse(response, safe=False)

    elif request.method == 'POST':
        body = json.loads(request.body)

        annual_goal = AnnualGoal.objects.create(
            user_id=body['user'],
            year=body['year'],
            target_books=body['target_books']
        )

        return JsonResponse({'id': annual_goal.id}, status=201)

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def annual_goal_by_id(request, id):
    try:
        annual_goal = AnnualGoal.objects.get(id=id)
    except AnnualGoal.DoesNotExist:
        return JsonResponse({'error': 'Not found'}, status=404)

    if request.method == 'GET':
        return JsonResponse({
            'id': annual_goal.id,
            'year': annual_goal.year,
            'target_books': annual_goal.target_books
        })

    elif request.method == 'PUT':
        body = json.loads(request.body)

        annual_goal.year = body.get('year', annual_goal.year)
        annual_goal.target_books = body.get('target_books', annual_goal.target_books)
        annual_goal.save()

        return JsonResponse({'updated': True})

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

class AuthorViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = Author.objects.all()
    serializer_class = AuthorSerializer

class GenreViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = Genre.objects.all()
    serializer_class = GenreSerializer

@csrf_exempt
def readings(request):
    if request.method == 'GET':
        user_id = request.GET.get('user', None)

        readings = Reading.objects.all()
        if user_id:
            readings = readings.filter(user_id=user_id)

        response = []
        for r in readings:
            response.append({
                'id': r.id,
                'book': r.book.title,
                'status': r.status,
                'start_date': r.start_date,
                'end_date': r.end_date
            })

        return JsonResponse(response, safe=False)

    elif request.method == 'POST':
        body = json.loads(request.body)

        reading = Reading.objects.create(
            user_id=body['user'],
            book_id=body['book'],
            status=body['status']
        )

        return JsonResponse({'id': reading.id}, status=201)

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

@csrf_exempt
def reading_by_id(request, id):
    try:
        reading = Reading.objects.get(id=id)
    except Reading.DoesNotExist:
        return JsonResponse({'error': 'Not found'}, status=404)

    if request.method == 'GET':
        return JsonResponse({
            'id': reading.id,
            'book': reading.book.title,
            'status': reading.status
        })

    elif request.method == 'PUT':
        body = json.loads(request.body)

        reading.status = body.get('status', reading.status)
        reading.save()

        return JsonResponse({'updated': True})

    elif request.method == 'DELETE':
        reading.delete()
        return JsonResponse({'deleted': True})

    return JsonResponse({'error': 'Unsupported HTTP method'}, status=405)

class ReadingSessionViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = ReadingSession.objects.all()
    serializer_class = ReadingSessionSerializer

    def get_queryset(self):
        return ReadingSession.objects.filter(reading__user=self.request.user)



